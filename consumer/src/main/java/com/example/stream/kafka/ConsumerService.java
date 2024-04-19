package com.example.stream.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.example.stream.Product;
import com.example.stream.mock.MockService;

@Service
public class ConsumerService {

	@Autowired
	private ProducerService producerService;
	@Autowired
	private MockService mockService;

	private final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

	@Retryable(retryFor = { HttpServerErrorException.class,
			Unauthorized.class,
			CannotCreateTransactionException.class
		}, maxAttemptsExpression = "${variable.retry.max-attempts}", 
		   backoff = @Backoff(delayExpression = "${variable.retry.delay}"))
	public void consumerHandler(Product product) {
		LOGGER.info("recived - {}", product.getName());
		try {
			Integer num = mockService.send(product);
			producerService.producer(num);
		} catch (CannotCreateTransactionException ex) {
			LOGGER.error("couldn't connect to DB: {}", ex.getMessage());
			throw ex;
		} catch (Unauthorized ex) {
			LOGGER.error("You are unauthorized: {}", ex.getCause());
			throw ex;
		} catch (HttpServerErrorException ex) {
			LOGGER.error("HTTP server error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
			LOGGER.error("Response body: " + ex.getResponseBodyAsString());
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("An error occurred: " + ex.getMessage());
		}
	}

	@Recover
	public void recoverHandler(Unauthorized ex, Product product) {
		LOGGER.error("Yoy are unauthorized many times", ex.getMessage());
	}

	@Recover
	public void recoverHandler(HttpServerErrorException ex, Product producta) {
		LOGGER.error("couldn't send request", ex.getMessage());
	}

	@Recover
	public void recoverHandler(CannotCreateTransactionException ex, Product product) {
		LOGGER.error("couldn't connect to DB many times", ex);
	}

}
