package com.trainticket.trip.kafka;

import com.trainticket.common.event.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.trip-completed}")
    private String tripCompletedTopic;

    public void publishTripCompleted(TripCompletedEvent event) {
        kafkaTemplate.send(tripCompletedTopic, event.getPayload().getTripId(), event);
    }
}
