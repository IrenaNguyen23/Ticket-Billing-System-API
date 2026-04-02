package com.trainticket.auth.service;

import com.trainticket.auth.dto.AuthResponse;
import com.trainticket.auth.dto.LoginRequest;
import com.trainticket.auth.dto.RegisterRequest;
import com.trainticket.auth.entity.RefreshToken;
import com.trainticket.auth.entity.User;
import com.trainticket.auth.repository.RefreshTokenRepository;
import com.trainticket.auth.repository.UserRepository;
import com.trainticket.common.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Value("${security.jwt.refresh-ttl-days:7}")
    private long refreshTtlDays;

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered", HttpStatus.CONFLICT);
        });

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .build();
        userRepository.save(user);

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid login", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid login", HttpStatus.UNAUTHORIZED);
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("TOKEN_EXPIRED", "Refresh token invalid", HttpStatus.UNAUTHORIZED));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("TOKEN_EXPIRED", "Refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(user);
    }

    public void logout(String accessToken, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            String hash = hashToken(refreshToken);
            refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }

        if (accessToken != null && !accessToken.isBlank()) {
            String jti = jwtService.extractTokenId(accessToken);
            long ttlSeconds = jwtService.extractRemainingTtlSeconds(accessToken);
            if (jti != null && ttlSeconds > 0) {
                redisTemplate.opsForValue().set("blacklist:" + jti, "1", ttlSeconds, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Instant expiresAt = Instant.now().plus(refreshTtlDays, ChronoUnit.DAYS);

        RefreshToken entity = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .tokenHash(hashToken(refreshToken))
                .expiresAt(expiresAt)
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(entity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresInSeconds(jwtService.getAccessTtlSeconds())
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash token", e);
        }
    }
}
