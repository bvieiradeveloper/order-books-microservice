package br.com.com.microservices.orchestrated.orchestratorservice.core.service;

import br.com.com.microservices.orchestrated.orchestratorservice.core.dto.Event;
import br.com.com.microservices.orchestrated.orchestratorservice.core.dto.History;
import br.com.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import br.com.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import br.com.com.microservices.orchestrated.orchestratorservice.core.producer.KafkaProducer;
import br.com.com.microservices.orchestrated.orchestratorservice.core.saga.SagaExecutionController;
import br.com.com.microservices.orchestrated.orchestratorservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {
    private final SagaExecutionController sagaExecutionController;
    private final KafkaProducer producer;
    private final JsonUtil jsonUtil;

    public void startSaga(Event event){
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED!");
        addHistory(event, "Saga started!");
        sendToProducerWithTopic(event, topic);
    }

    private void sendToProducerWithTopic(Event event, ETopics topic) {
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private void addHistory(Event event, String message) {
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToEventHistory(history);
    }

    private ETopics getTopic(Event event) {
        return sagaExecutionController.getNextTopic(event);
    }
}
