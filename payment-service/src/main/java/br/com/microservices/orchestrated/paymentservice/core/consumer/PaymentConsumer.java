package br.com.microservices.orchestrated.paymentservice.core.consumer;

import br.com.microservices.orchestrated.paymentservice.core.service.PaymentService;
import br.com.microservices.orchestrated.paymentservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private  final JsonUtil jsonUtil;
    private final PaymentService service;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"
    )
    public void consumerPaymentSuccessEvent(String payload){
        log.info("Receiving payment success event {} from payment-success topic",payload);
        service.realizePayment(jsonUtil.toEvent(payload));
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-fail}"
    )
    public void consumerPaymentFailEvent(String payload) {
        log.info("Receiving payment fail event {} from payment-fail topic",payload);
        service.realizeRefund(jsonUtil.toEvent(payload));
    }
}
