package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus;
import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;


@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private final ValidationRepository validationRepository;
    private final ProductRepository productRepository;
    private final KafkaProducer producer;
    private final JsonUtil jsonUtil;

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    public void validateExistingProducts(Event event){
        try{
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to validate products: ",ex);
            handleFailedCurrentNotExecuted(event, ex.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleFailedCurrentNotExecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event,"Fail to validate products: ".concat(message));
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products are validated successfully!");
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

    private void createValidation(Event event, boolean success) {
        var validation = Validation
                .builder()
                .success(success)
                .orderId(event.getPayload().getId())
                .transactionId(event.getPayload().getTransactionId())
                .build();

        validationRepository.save(validation);
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(),
                                                                 event.getPayload().getTransactionId())){
            throw new ValidationException("There's another transactionId for this validation.!");
        }

        event.getPayload().getProducts().forEach(orderProduct -> {
            validateProductInformed(orderProduct);
            validateExistingProduct(orderProduct.getProduct().getCode());
        });
    }

    private void validateProductsInformed(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
            throw  new ValidationException("Product list is empty!");
        }

        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())){
            throw  new ValidationException("OrderID and TransactionID must be informed!!");
        }
    }

    private void validateProductInformed(OrderProducts product){
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())){
            throw  new ValidationException("Product must be informed!");
        }
    }

    private void validateExistingProduct(String code){
        if (!productRepository.existsByCode(code)){
            throw new ValidationException("Product with code " + code + " does not exist!");
        }
    }

    public void rollbackEvent(Event event){
        changeValidateFail(event);
        event.setSource(CURRENT_SOURCE);
        event.setStatus(ESagaStatus.FAIL);
        addHistory(event, "Rollback executed on product validation!");
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeValidateFail(Event event) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getPayload().getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(false);
                    validationRepository.save(validation);
                }, () -> createValidation(event, false));
    }
}
