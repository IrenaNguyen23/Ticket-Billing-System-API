package com.trainticket.auth.controller;

import com.trainticket.auth.dto.AuthResponse;
import com.trainticket.auth.dto.LoginRequest;
import com.trainticket.auth.dto.RegisterRequest;
import com.trainticket.auth.service.AuthService;
import com.trainticket.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return ApiResponse.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
                       @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        String accessToken = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : null;
        authService.logout(accessToken, refreshToken);
    }

    @GetMapping("/me")
    public ApiResponse<String> me() {
        return ApiResponse.ok("stub-user");
    }
}
