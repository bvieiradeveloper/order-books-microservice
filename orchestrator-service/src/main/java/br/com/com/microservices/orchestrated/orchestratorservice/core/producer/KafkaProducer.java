package br.com.com.microservices.orchestrated.orchestratorservice.core.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String payload, String topic){
        try{
            kafkaTemplate.send(topic, payload);
            log.info("Send event to the topic {} with data {}", topic, payload);
        } catch (Exception ex) {
            log.error("Error trying to send data to topic {} with data {}", topic, payload, ex);
        }
    }
}
