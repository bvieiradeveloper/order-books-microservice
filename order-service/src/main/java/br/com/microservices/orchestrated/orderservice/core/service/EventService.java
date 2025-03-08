package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilter;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository repository;

    public Event findByFilter(EventFilter filter){
        ValidateEmptyFilter(filter);
        if (!isEmpty(filter.getOrderId())){
            return getEventByOrderId(filter);
        }
        return getEventByTransactionId(filter);
    }

    public List<Event> findAll(){
        return  repository.findAllByOrderByCreatedAtDesc();
    }

    private Event getEventByTransactionId(EventFilter filter) {
        return repository.findTop1ByTransactionIdOrderByCreatedAtDesc(filter.getTransactionId())
                .orElseThrow(() -> new ValidationException("Order not found"));
    }

    private Event getEventByOrderId(EventFilter filter) {
        return repository.findTop1ByOrderIdOrderByCreatedAtDesc(filter.getOrderId())
                .orElseThrow(() -> new ValidationException("Order not found"));
    }

    private static void ValidateEmptyFilter(EventFilter filter) {
        if (isEmpty(filter.getOrderId()) && isEmpty(filter.getTransactionId())){
            throw new ValidationException("order id or transaction id must be informed!");
        }
    }

    public Event save(Event event){
        return repository.save(event);
    }
}
