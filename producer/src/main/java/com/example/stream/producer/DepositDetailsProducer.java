package com.example.stream.producer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class DepositDetailsProducer {
    
    @Autowired
    private StreamBridge streamBridge;

    @Value("${variable.kafka.producer.binding-name-1}")
    private String bindingName;

    public void send() {
       // TODO LATER 
    }

}
