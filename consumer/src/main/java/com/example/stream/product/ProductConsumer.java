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
    private ProductEntityService productEntityService;

    private final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);
   
    @Retryable(
        retryFor = {CannotCreateTransactionException.class}, 
        maxAttemptsExpression = "${variable.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${variable.retry.delay}")
    )
    public void getFromTopicProduct(List<Product> products) {
        products.forEach(product -> {
            LOGGER.info("get product --- {}", product.getId());
            try {
                productEntityService.saveProduct(product);
                LOGGER.info("save products");
            } catch(CannotCreateTransactionException ex) {
                LOGGER.error("couldn't connect to DB: {}", ex.getMessage());
                throw ex;
            } catch (Exception ex) {
                LOGGER.error("An error occured");
                ex.printStackTrace();
            }
        });      
    }

    @Recover
    public void recoverDB(CannotCreateTransactionException ex, List<Product> products) {
        LOGGER.error("connection to db are failed");
    }
}
