package br.com.com.microservices.orchestrated.orchestratorservice.core.consumer;

import br.com.com.microservices.orchestrated.orchestratorservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrchestratorConsumer {
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    )
    public void consumerStartSageEvent(String payload) {
        log.info("Receiving start saga event {} from start-saga topic",payload);
        var event = jsonUtil.toEvent(payload);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.orchestrator}"
    )
    public void consumerOrchestratorEvent(String payload) {
        log.info("Receiving orchestrator event {} from orchestrator topic",payload);
        var event = jsonUtil.toEvent(payload);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"
    )
    public void consumerFinishSuccessEvent(String payload) {
        log.info("Receiving finish success event {} from finish-success topic",payload);
        var event = jsonUtil.toEvent(payload);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-fail}"
    )
    public void consumerFinishFailEvent(String payload) {
        log.info("Receiving finish fail event {} from finish-fail topic",payload);
        var event = jsonUtil.toEvent(payload);
    }
}
