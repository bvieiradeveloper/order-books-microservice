package br.com.com.microservices.orchestrated.orchestratorservice.core.dto;

import br.com.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private EEventSource source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addToEventHistory(History history) {
        if (eventHistory == null) {
            eventHistory = new ArrayList<>();
        }

        eventHistory.add(history);
    }
}
