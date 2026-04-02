package com.trainticket.payment.kafka;

import com.trainticket.common.event.PaymentProcessedEvent;
import com.trainticket.common.event.PaymentRequestedEvent;
import com.trainticket.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    private final PaymentService paymentService;
    private final PaymentResultProducer paymentResultProducer;

    @KafkaListener(topics = "${app.kafka.topics.payment-requested}")
    public void onPaymentRequested(PaymentRequestedEvent event) {
        PaymentProcessedEvent result = paymentService.handlePaymentRequest(event);
        paymentResultProducer.publish(result);
    }
}
