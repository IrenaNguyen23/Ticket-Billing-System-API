package com.trainticket.billing.service;

import com.trainticket.common.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.payment-requested}")
    private String paymentRequestedTopic;

    public void publish(PaymentRequestedEvent event) {
        kafkaTemplate.send(paymentRequestedTopic, event.getPayload().getBillingId(), event);
    }
}
