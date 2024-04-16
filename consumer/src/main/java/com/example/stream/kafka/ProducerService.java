package com.example.stream.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.example.stream.user.UserRepository;

@Component
public class ProducerService {
    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private UserRepository userRepository;

    @Value("${variable.binding-name}")
    private String bindingName;

    private final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

    public void producer(Integer num) {
        userRepository.findAll().forEach(user -> {
            streamBridge.send(bindingName, user);
        });

        LOGGER.info("send to kafka all users");
    }
}
