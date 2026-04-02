package com.trainticket.billing.kafka;

import com.trainticket.billing.service.BillingService;
import com.trainticket.common.event.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripCompletedConsumer {
    private final BillingService billingService;

    @KafkaListener(topics = "${app.kafka.topics.trip-completed}")
    public void onTripCompleted(TripCompletedEvent event) {
        billingService.handleTripCompleted(event);
    }
}
