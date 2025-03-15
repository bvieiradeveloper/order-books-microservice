package br.com.com.microservices.orchestrated.inventoryservice.core.service;

import br.com.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException;
import br.com.com.microservices.orchestrated.inventoryservice.core.dto.Event;
import br.com.com.microservices.orchestrated.inventoryservice.core.dto.History;
import br.com.com.microservices.orchestrated.inventoryservice.core.dto.Order;
import br.com.com.microservices.orchestrated.inventoryservice.core.dto.OrderProducts;
import br.com.com.microservices.orchestrated.inventoryservice.core.enums.ESagaStatus;
import br.com.com.microservices.orchestrated.inventoryservice.core.model.Inventory;
import br.com.com.microservices.orchestrated.inventoryservice.core.model.OrderInventory;
import br.com.com.microservices.orchestrated.inventoryservice.core.producer.KafkaProducer;
import br.com.com.microservices.orchestrated.inventoryservice.core.repository.InventoryRepository;
import br.com.com.microservices.orchestrated.inventoryservice.core.repository.OrderInventoryRepository;
import br.com.com.microservices.orchestrated.inventoryservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;
    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;

    public void updateInventory(Event event){
        try{
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventory(event.getPayload());
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to update inventory: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }

        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to update inventory: ".concat(message));
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Inventory updated successfully!");
    }

    private void updateInventory(Order order) {
       order.getProducts().forEach( product ->{
           var inventory = findInventoryByProductCode(product.getProduct().getCode());
           checkInventory(inventory.getAvailable(), product.getQuantity());
           inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
           inventoryRepository.save(inventory);
       });
    }

    private void checkInventory(Integer available, int orderQuantity) {
        if (orderQuantity > available) {
            throw new ValidationException("Product is out of stock!");
        }
    }

    private void createOrderInventory(Event event) {
        event
                .getPayload()
                .getProducts()
                .forEach( product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    var orderInventory = createOrderInventory(event, product, inventory);
                    orderInventoryRepository.save(orderInventory);
                });

    }

    private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory) {
        return OrderInventory
                .builder()
                .inventory(inventory)
                .orderId(event.getPayload().getId())
                .transactionId(event.getPayload().getTransactionId())
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getAvailable() - product.getQuantity())
                .build();
    }

    private Inventory findInventoryByProductCode(String code) {
        return inventoryRepository
                .findByProductCode(code)
                .orElseThrow(() -> new ValidationException("Inventory not found by informed product."));
    }

    private void checkCurrentValidation(Event event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(
                event.getPayload().getId(), event.getPayload().getTransactionId())){
            throw new ValidationException("There's another transactionId for this validation.");
        }
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

    public void returnInventoryToPreviousValues(Event event){
        orderInventoryRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .forEach( orderInventory -> {
                    var inventory = orderInventory.getInventory();
                    inventory.setAvailable(orderInventory.getOldQuantity());
                    inventoryRepository.save(inventory);
                    log.info("Restored inventory for order {}: from {} to {}",
                            event.getPayload().getId(), orderInventory.getNewQuantity(), inventory.getAvailable());
                });
    }
}
