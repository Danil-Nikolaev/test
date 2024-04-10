package com.example.stream;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {

	private final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

	@Value("${variable.profile}")
	private String profile; 
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}


	@Bean
	public Consumer<Product> consumer() {
		return product -> {
			LOGGER.info("profile - {}", profile);
			LOGGER.info("recived {}", product.getName());	
		};
	}
}
