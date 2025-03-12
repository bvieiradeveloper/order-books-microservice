package br.com.microservices.orchestrated.paymentservice.consumer.dto;

import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.productvalidationservice.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.dto.Order;
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
    private String source;
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
