package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    
    @Value("${variable.bindingName}")
    private String bindingName;

    @Value("${variable.profile}")
    private String profile;

    @Autowired
    private StreamBridge streamBridge;
    private final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    public void producer(Product product) {
        LOGGER.info("profile - {}", profile);
        streamBridge.send(bindingName, product);
        LOGGER.info("send product {}", product.getName());
    }
}
