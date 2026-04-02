package com.trainticket.payment.controller;

import com.trainticket.common.dto.ApiResponse;
import com.trainticket.payment.dto.BalanceResponse;
import com.trainticket.payment.dto.TopUpRequest;
import com.trainticket.payment.entity.Transaction;
import com.trainticket.payment.repository.TransactionRepository;
import com.trainticket.payment.service.PaymentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final TransactionRepository transactionRepository;

    @GetMapping("/balance")
    public ApiResponse<BalanceResponse> balance(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(paymentService.getBalance(userId));
    }

    @PostMapping("/topup")
    public ApiResponse<BalanceResponse> topup(Authentication authentication,
                                              @Valid @RequestBody TopUpRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(paymentService.topup(userId, request.getAmount()));
    }

    @GetMapping("/transactions")
    public ApiResponse<Page<Transaction>> transactions(Authentication authentication, Pageable pageable) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(transactionRepository.findByUserId(userId, pageable));
    }
}
