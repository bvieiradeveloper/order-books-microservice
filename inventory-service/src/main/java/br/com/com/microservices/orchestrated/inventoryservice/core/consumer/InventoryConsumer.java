package br.com.com.microservices.orchestrated.inventoryservice.core.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void consumerInventorySuccessEvent(String payload){
        log.info("Receiving inventory success event {} from inventory-success topic",payload);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void consumerInventoryFailEvent(String payload){
        log.info("Receiving inventory fail event {} from inventory-fail topic",payload);
    }
}
