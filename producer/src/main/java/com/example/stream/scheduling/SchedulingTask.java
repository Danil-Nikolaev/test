package com.example.stream.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.stream.product.ProductService;

@Component
public class SchedulingTask {

    @Autowired
    private ProductService productService;    

    private final Logger LOGGER = LoggerFactory.getLogger(SchedulingTask.class);

    @Scheduled(fixedRateString = "${variable.scheduling.fixed-rate}")
    public void unloadDBScheduled() {
        LOGGER.info("scheduled start");
        productService.unloadDB();
    }
}
