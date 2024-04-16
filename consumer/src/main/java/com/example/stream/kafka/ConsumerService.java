package com.example.stream.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    @Retryable(retryFor = {HttpServerErrorException.class, Unauthorized.class})
	public void consumerHandler(Product product) {
		LOGGER.info("recived - {}", product.getName());
		try {
			Integer num = mockService.send(product);
			producerService.producer(num);
		} catch(Unauthorized ex) {
			LOGGER.error("You are unauthorized", ex);
			throw ex;
		} catch (HttpClientErrorException ex) {
			LOGGER.error("HTTP error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
			LOGGER.error("Response body: " + ex.getResponseBodyAsString());
		} catch (HttpServerErrorException ex) {
			LOGGER.error("HTTP server error occurred: " + ex.getStatusCode() + " - " + ex.getStatusText());
			LOGGER.error("Response body: " + ex.getResponseBodyAsString());
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("An error occurred: " + ex.getMessage(), ex);
		}
	}

	@Recover
	public void recoverConsumerHandler1(Unauthorized ex, Product product) {
		LOGGER.error("THHIS IS WORRRK");
	}

	@Recover
	public void recoverConsumerHandler(HttpServerErrorException ex, Product product) {
		LOGGER.error("не получилось снова");
	}

}
