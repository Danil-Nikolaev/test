package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class MockService {
   
    @Value("${variable.mock.base-url}")
    private String baseUrl;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private RestTemplate restTemplate;

    HttpHeaders httpHeaders = new HttpHeaders();

    private final Logger LOGGER = LoggerFactory.getLogger(MockService.class); 

    public Integer send(Product product) {
        ResponseEntity<Integer> response = null;
        String url = baseUrl + "/test";
        try{
            response = restTemplate.postForEntity(url, new HttpEntity<>(product, httpHeaders),Integer.class);
            return response.getBody();
        } catch(HttpClientErrorException ex){
            LOGGER.error("HTTP error occurred in MockService : " + ex.getStatusCode() + " - " + ex.getStatusText());
            LOGGER.error("Response body: " + ex.getResponseBodyAsString());
            String accessToken = getAccessToken();
            if (accessToken != null) {
                httpHeaders.add("auth", accessToken);    
                response = restTemplate.postForEntity(url, new HttpEntity<>(product, httpHeaders),Integer.class);
                return response.getBody();
            }
        } catch(HttpServerErrorException ex) {
            LOGGER.error("HTTP server error occurred in MockSevice: " + ex.getStatusCode() + " - " + ex.getStatusText());
            LOGGER.error("Response body: " + ex.getResponseBodyAsString());
        } catch(Exception ex) {
            LOGGER.error("An error occurred in KeycloakService: " + ex.getMessage(), ex);
        }
        return null;
    }

    private String getAccessToken() {
        return keycloakService.getAccessToken();
    }
}
