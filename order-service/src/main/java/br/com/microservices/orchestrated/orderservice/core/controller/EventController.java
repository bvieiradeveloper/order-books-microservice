package br.com.microservices.orchestrated.orderservice.core.controller;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilter;
import br.com.microservices.orchestrated.orderservice.core.service.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Event findByFilter(EventFilter filters){
        return  eventService.findByFilter(filters);
    }
    @GetMapping("all")
    public List<Event> findAll(){
        return eventService.findAll();
    }
}
