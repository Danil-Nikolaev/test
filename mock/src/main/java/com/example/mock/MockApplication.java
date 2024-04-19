package com.example.mock;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@SpringBootApplication
@RestController
@RequestMapping("/test")
public class MockApplication {
	
	Random random = new Random();

	private final Logger LOGGER = LoggerFactory.getLogger(MockApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(MockApplication.class, args);
	}

	private int countRequest = 0;

	@PostMapping
	public ResponseEntity<Integer> getMethodName(@RequestHeader(value = "auth", required = false) String auth,
	@RequestBody Product product) {
		LOGGER.info("get product - {}",product.getName());
		if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		
		if (countRequest > 1) countRequest = 0;
		
		if (countRequest > 7) {
			countRequest++;
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		if (countRequest++ > 5) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
		
		Integer num = random.nextInt();
		LOGGER.info("send num - {}", num);
		return ResponseEntity.ok(num);
	}
	
}
