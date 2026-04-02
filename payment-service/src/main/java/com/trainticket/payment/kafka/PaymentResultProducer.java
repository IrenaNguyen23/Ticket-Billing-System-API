package com.trainticket.payment.kafka;

import com.trainticket.common.event.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.payment-processed}")
    private String paymentProcessedTopic;

    @Value("${app.kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    public void publish(PaymentProcessedEvent event) {
        if (event == null) {
            return;
        }
        String key = event.getPayload() != null ? event.getPayload().getBillingId() : null;
        if ("PAYMENT_FAILED".equals(event.getEventType())) {
            kafkaTemplate.send(paymentFailedTopic, key, event);
        } else {
            kafkaTemplate.send(paymentProcessedTopic, key, event);
        }
    }
}
