package com.example.stream.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Component
public class ProductConsumer {

    @Autowired
    private ProductEntityRepository productEntityRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);

    public void getFromTopicProduct(List<ProductEntity> products) {
        getProduct(products);
    }

    @Retryable(
        retryFor = { CannotCreateTransactionException.class }, 
        maxAttemptsExpression = "${variable.retry.max-attempts}", 
        backoff = @Backoff(delayExpression = "${variable.retry.delay}")
        )
    public void getProduct(List<ProductEntity> products) {
        try {
            productEntityRepository.saveAll(products);
            LOGGER.info("save message with products");
        } catch(CannotCreateTransactionException ex) {
            LOGGER.error("coldn't connect to DB: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("An error occuraed: {}", ex.getMessage());
        }
    }

    @Recover
    public void recoverDB(CannotCreateTransactionException ex, List<ProductEntity> products) {
        LOGGER.error("connection to db are failed");
    }
}
