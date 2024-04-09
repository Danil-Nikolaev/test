package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    
    @Autowired
    private StreamBridge streamBridge;
    private final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    public void producer(Product product) {
        streamBridge.send("producer-out-0", product);
        LOGGER.info("send product {}", product.getName());
    }
}
