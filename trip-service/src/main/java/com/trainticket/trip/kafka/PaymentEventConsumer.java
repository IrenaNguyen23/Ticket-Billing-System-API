package com.trainticket.trip.kafka;

import com.trainticket.common.event.PaymentProcessedEvent;
import com.trainticket.trip.entity.TripStatus;
import com.trainticket.trip.repository.TripRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final TripRepository tripRepository;

    @KafkaListener(topics = "${app.kafka.topics.payment-processed}")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        updateTripStatus(event, TripStatus.COMPLETED);
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-failed}")
    public void onPaymentFailed(PaymentProcessedEvent event) {
        updateTripStatus(event, TripStatus.PAYMENT_FAILED);
    }

    private void updateTripStatus(PaymentProcessedEvent event, TripStatus status) {
        if (event == null || event.getPayload() == null) {
            return;
        }
        UUID tripId = UUID.fromString(event.getPayload().getTripId());
        tripRepository.findById(tripId).ifPresent(trip -> {
            trip.setStatus(status);
            tripRepository.save(trip);
        });
    }
}
