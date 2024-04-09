package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	private static final Logger LOG = LoggerFactory.getLogger(ConsumerApplication.class);

	@Bean
	public Consumer<Product> consumer() {
		return product -> LOG.info("Received: {}", product.getName());
	}

}
