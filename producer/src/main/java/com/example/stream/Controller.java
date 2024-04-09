package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class Controller {

    private final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private Producer producer;

    @PostMapping
    public void createProduct(@RequestBody Product product) {
        producer.sendMessage(product);
        LOGGER.info("Create product with {}", product.getName());
    }
}
