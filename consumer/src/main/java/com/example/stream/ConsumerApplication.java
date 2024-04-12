package com.example.stream;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@SpringBootApplication
public class ConsumerApplication {

	@Autowired
	private MockService mockService;

	@Autowired
	private ProducerService producerService;

	private final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

	private int countAttempt = 0;
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Bean
	public Consumer<Product> consumer() {
		return product -> {
			LOGGER.info("recived - {}", product.getName());
			try {
				Integer num = mockService.send(product);
				producerService.producer(num);
				countAttempt = 0;
			} catch (HttpClientErrorException ex) {
				LOGGER.error("HTTP error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
				LOGGER.error("Response body: " + ex.getResponseBodyAsString());
			} catch (HttpServerErrorException ex) {
				LOGGER.error("HTTP server error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
				LOGGER.error("Response body: " + ex.getResponseBodyAsString());
				if (countAttempt++ < 3) 
					consumer().notify();
			} catch (Exception ex) {
				LOGGER.error("An error occurred: " + ex.getMessage(), ex);
			}
		};
	}
}
