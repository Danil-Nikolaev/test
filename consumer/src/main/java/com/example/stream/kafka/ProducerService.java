package com.example.stream.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @Value("${variable.db.page-size}")
    private int pageSize;

    private final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

    public void producer(Integer num) {
        Long lastProcessedId = 0L;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id"));

        Slice<User> users;
        do {
            users = userRepository.findByIdGreaterThan(lastProcessedId, pageable);
            for (User user : users) {
                streamBridge.send(bindingName, user);
                lastProcessedId = user.getId();
            }
        } while (!users.isEmpty());

        LOGGER.info("send to kafka all users");
    }
}
