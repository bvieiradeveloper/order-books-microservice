package br.com.microservices.orchestrated.productvalidationservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.orchestrator}")
    private String orchestratorTopic;

    public void sendEvent(String payload){
        try {
            kafkaTemplate.send(orchestratorTopic, payload);
            log.info("Send event to the topic {} with data {}", orchestratorTopic, payload);
        } catch (Exception e) {
            log.error("Error trying to send data to topic {} with data {}", orchestratorTopic, payload, e);
        }
    }
}
