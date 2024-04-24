package com.example.stream.scheduling;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.stream.product.ProductService;

@Component
public class SchedulingTask {

    @Autowired
    private ProductService productService;    

    private final Logger LOGGER = LoggerFactory.getLogger(SchedulingTask.class);
    
    @Scheduled(fixedRateString = "${variable.scheduling.fixed-rate}")
    @Retryable(retryFor = {DataAccessResourceFailureException.class})
    public void unloadDBScheduled() {
        LOGGER.info("scheduled start");
        try {
            productService.unloadDB();
        } catch (DataAccessResourceFailureException ex) {
            LOGGER.error("couldn't connect to db: {}", ex.getMessage());
            throw ex;
        } catch(ExecutionException ex) {
            LOGGER.error("Execution exception: {}", ex.getCause());
        } catch(InterruptedException ex) {
            LOGGER.error("Interrupted Exception: {}", ex.getMessage());
        } catch(NullPointerException ex) {
            LOGGER.error("Null prointer : {}", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("An error occured: {}", ex.getMessage());
        }
    }

    @Recover
    public void recoverDB(DataAccessResourceFailureException ex) {
        LOGGER.error("connection to DB are failed");
    }
}
