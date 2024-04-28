package com.example.stream.producer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.example.stream.model.Product;

@Component
public class TestKafkaProducer {
    
    @Autowired
    private StreamBridge streamBridge;

    @Value("${variable.kafka.producer.binding-name-0}")
    private String bindingName;

    public void send(List<Product> products) {
        streamBridge.send(bindingName, products);
    }
}
