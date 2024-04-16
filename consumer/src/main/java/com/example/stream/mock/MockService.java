package com.example.stream.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.stream.Product;
import com.example.stream.keycloak.KeycloakService;

@Component
public class MockService {

    @Value("${variable.mock.post-endpoint}")
    private String postEndpoint;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders httpHeaders = new HttpHeaders();
    private final Logger LOGGER = LoggerFactory.getLogger(MockService.class);

    public Integer send(Product product) {
        httpHeaders.clear();
        httpHeaders.add("auth", getAccessToken());
        ResponseEntity<Integer> response = restTemplate.postForEntity(postEndpoint, new HttpEntity<>(product, httpHeaders), Integer.class);

        LOGGER.info("response from mock service - {}", response.getBody());
        return response.getBody();
    }

    private String getAccessToken() {
        return keycloakService.getAccessToken();
    }

}
