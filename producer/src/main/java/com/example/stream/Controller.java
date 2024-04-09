package com.example.stream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/products")
public class Controller {

    @Autowired
    private Producer producer;
    private final Logger LOGGER = LoggerFactory.getLogger(Controller.class); 
    @PostMapping
    public Product postMethodName(@RequestBody Product product) {
        producer.producer(product);
        LOGGER.info("{}", product.getName());
        return product;
    }
    
    
}
