package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.dto.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductValidationService {

    public void validateExistingProducts(Event event){
        try{

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
