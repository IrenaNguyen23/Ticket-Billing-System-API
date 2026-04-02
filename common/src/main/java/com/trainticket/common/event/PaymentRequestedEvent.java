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
public class PaymentRequestedEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private PaymentRequestedPayload payload;

    public static PaymentRequestedEvent of(PaymentRequestedPayload payload) {
        return PaymentRequestedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PAYMENT_REQUESTED")
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequestedPayload {
        private String billingId;
        private String tripId;
        private String userId;
        private long amount;
        private String currency;
    }
}
