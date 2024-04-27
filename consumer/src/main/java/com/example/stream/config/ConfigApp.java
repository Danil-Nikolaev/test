package com.example.stream.config;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.stream.product.ProductConsumer;
import com.example.stream.product.ProductEntity;

@Configuration
public class ConfigApp {
    @Autowired
    private ProductConsumer productConsumer;
    private final Logger LOGGER = LoggerFactory.getLogger(ConfigApp.class);
  
    @Bean
	Consumer<List<ProductEntity>> consumer() {
        LOGGER.info("getConsumer");
		return productConsumer::getFromTopicProduct;
	}
}
