package com.example.stream;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {

	@Autowired
	private MockService mockService;

	@Autowired
	private ProducerService producerService;	

	private final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}


	@Bean
	public Consumer<Product> consumer() {
		return product -> {
			LOGGER.info("recived - {}", product.getName());	
			Integer num = mockService.send(product);
			if (num == null) return;
			producerService.producer(num);	
		};
	}

}
