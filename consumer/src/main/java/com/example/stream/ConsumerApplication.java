package com.example.stream;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

import com.example.stream.kafka.ConsumerService;

@SpringBootApplication
@EnableRetry
public class ConsumerApplication {

	@Autowired
	private ConsumerService consumerService;
	
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}	
	
	@Bean
	public Consumer<Product> consumer() {
		return consumerService::consumerHandler;
	}
}
