package com.trainticket.common.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private PaymentProcessedPayload payload;

    public static PaymentProcessedEvent success(PaymentProcessedPayload payload) {
        return PaymentProcessedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PAYMENT_PROCESSED")
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }

    public static PaymentProcessedEvent failed(PaymentProcessedPayload payload) {
        return PaymentProcessedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PAYMENT_FAILED")
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentProcessedPayload {
        private String billingId;
        private String tripId;
        private String userId;
        private long amount;
        private String currency;
        private String status;
        private String failureReason;
    }
}
