package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class ProducerService {
    @Autowired
    private StreamBridge streamBridge;

    @Value("${variable.binding-name}")
    private String bindingName;

    private final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

    public void producer(Integer num) {
        if (num % 2 == 0) {
            Message<Integer> message = MessageBuilder
                                .withPayload(num)
                                .setHeader(KafkaHeaders.KEY, "even".getBytes())
                                .build();
            streamBridge.send(bindingName, message);
            LOGGER.info("send even num - ", num);
            return;
        }
        streamBridge.send(bindingName, num);
        LOGGER.info("send num - {}", num);
    }
}
