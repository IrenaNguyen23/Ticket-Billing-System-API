package com.trainticket.billing.service;

import com.trainticket.billing.entity.BillingRecord;
import com.trainticket.billing.repository.BillingRepository;
import com.trainticket.common.event.PaymentRequestedEvent;
import com.trainticket.common.event.TripCompletedEvent;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final BillingRepository billingRepository;
    private final PricingApiClient pricingApiClient;
    private final StringRedisTemplate redisTemplate;
    private final PaymentRequestProducer paymentRequestProducer;

    public void handleTripCompleted(TripCompletedEvent event) {
        if (event == null || event.getPayload() == null) {
            return;
        }

        String fromId = event.getPayload().getCheckinStationId();
        String toId = event.getPayload().getCheckoutStationId();
        String cacheKey = "route_price:" + fromId + ":" + toId;

        Long fare = null;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            fare = Long.parseLong(cached);
        }

        PricingApiClient.PricingResponse pricing = null;
        String fareSource = "CACHE";
        if (fare == null) {
            pricing = pricingApiClient.getFare(fromId, toId);
            fare = pricing != null ? pricing.getFare() : 0L;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(fare), Duration.ofHours(6));
            fareSource = "PRICING_API";
        }

        BillingRecord record = BillingRecord.builder()
                .id(UUID.randomUUID())
                .tripId(UUID.fromString(event.getPayload().getTripId()))
                .userId(UUID.fromString(event.getPayload().getUserId()))
                .amount(fare)
                .currency(pricing != null ? pricing.getCurrency() : "VND")
                .zoneFrom(pricing != null ? pricing.getZone() : null)
                .zoneTo(pricing != null ? pricing.getZone() : null)
                .fareSource(fareSource)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        BillingRecord saved = billingRepository.save(record);

        PaymentRequestedEvent.PaymentRequestedPayload payload = PaymentRequestedEvent.PaymentRequestedPayload.builder()
                .billingId(saved.getId().toString())
                .tripId(saved.getTripId().toString())
                .userId(saved.getUserId().toString())
                .amount(saved.getAmount())
                .currency(saved.getCurrency())
                .build();
        paymentRequestProducer.publish(PaymentRequestedEvent.of(payload));
    }
}
