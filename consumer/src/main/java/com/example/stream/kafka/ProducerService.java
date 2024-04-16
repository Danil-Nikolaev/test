package com.example.stream.kafka;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.example.stream.user.User;
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
        int pageSize = 10;

        Stream.iterate(0, pageNum -> pageNum + 1)
                .map(pageNum -> PageRequest.of(pageNum, pageSize))
                .map(userRepository::findAll)
                .takeWhile(page->!page.isEmpty())
                .forEach(page -> {
                    List<User> users = page.getContent();
                    users.forEach(user -> {
                        streamBridge.send(bindingName, user);
                    });
                });

        LOGGER.info("send to kafka all users");
    }
}
