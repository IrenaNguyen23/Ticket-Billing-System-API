package com.trainticket.payment.service;

import com.trainticket.common.event.PaymentProcessedEvent;
import com.trainticket.common.event.PaymentRequestedEvent;
import com.trainticket.payment.dto.BalanceResponse;
import com.trainticket.payment.entity.Transaction;
import com.trainticket.payment.entity.Wallet;
import com.trainticket.payment.repository.TransactionRepository;
import com.trainticket.payment.repository.WalletRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public BalanceResponse getBalance(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
        return new BalanceResponse(wallet.getBalance(), wallet.getCurrency());
    }

    @Transactional
    public BalanceResponse topup(UUID userId, long amount) {
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> createWallet(userId));
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .walletId(wallet.getId())
                .amount(amount)
                .type("CREDIT")
                .status("SUCCESS")
                .createdAt(Instant.now())
                .build();
        transactionRepository.save(tx);

        return new BalanceResponse(wallet.getBalance(), wallet.getCurrency());
    }

    @Transactional
    public PaymentProcessedEvent handlePaymentRequest(PaymentRequestedEvent event) {
        PaymentRequestedEvent.PaymentRequestedPayload payload = event.getPayload();
        UUID userId = UUID.fromString(payload.getUserId());
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> createWallet(userId));

        if (wallet.getBalance() < payload.getAmount()) {
            Transaction tx = Transaction.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .walletId(wallet.getId())
                    .tripId(UUID.fromString(payload.getTripId()))
                    .billingId(UUID.fromString(payload.getBillingId()))
                    .amount(payload.getAmount())
                    .type("DEBIT")
                    .status("FAILED")
                    .failureReason("INSUFFICIENT_BALANCE")
                    .createdAt(Instant.now())
                    .build();
            transactionRepository.save(tx);

            PaymentProcessedEvent.PaymentProcessedPayload out = PaymentProcessedEvent.PaymentProcessedPayload.builder()
                    .billingId(payload.getBillingId())
                    .tripId(payload.getTripId())
                    .userId(payload.getUserId())
                    .amount(payload.getAmount())
                    .currency(payload.getCurrency())
                    .status("FAILED")
                    .failureReason("INSUFFICIENT_BALANCE")
                    .build();
            return PaymentProcessedEvent.failed(out);
        }

        wallet.setBalance(wallet.getBalance() - payload.getAmount());
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .walletId(wallet.getId())
                .tripId(UUID.fromString(payload.getTripId()))
                .billingId(UUID.fromString(payload.getBillingId()))
                .amount(payload.getAmount())
                .type("DEBIT")
                .status("SUCCESS")
                .createdAt(Instant.now())
                .build();
        transactionRepository.save(tx);

        PaymentProcessedEvent.PaymentProcessedPayload out = PaymentProcessedEvent.PaymentProcessedPayload.builder()
                .billingId(payload.getBillingId())
                .tripId(payload.getTripId())
                .userId(payload.getUserId())
                .amount(payload.getAmount())
                .currency(payload.getCurrency())
                .status("SUCCESS")
                .build();
        return PaymentProcessedEvent.success(out);
    }

    private Wallet createWallet(UUID userId) {
        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .balance(0)
                .currency("VND")
                .build();
        return walletRepository.save(wallet);
    }
}
