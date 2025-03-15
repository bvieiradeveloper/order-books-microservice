package br.com.com.microservices.orchestrated.inventoryservice.core.consumer;

import br.com.com.microservices.orchestrated.inventoryservice.core.service.InventoryService;
import br.com.com.microservices.orchestrated.inventoryservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    private final InventoryService service;
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void consumerInventorySuccessEvent(String payload){
        log.info("Receiving inventory success event {} from inventory-success topic",payload);
        service.updateInventory(jsonUtil.toEvent(payload));
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void consumerInventoryFailEvent(String payload){
        log.info("Receiving inventory fail event {} from inventory-fail topic",payload);
        service.returnInventoryToPreviousValues(jsonUtil.toEvent(payload));
    }
}
