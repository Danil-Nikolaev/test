package com.example.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class KeycloakService {

    @Value("${variable.keycloak.base-url}")
    private String baseUrl;

    @Value("${variable.keycloak.realm-name}")
    private String realmName;

    @Value("${variable.keycloak.client-id}")
    private String clientId;

    @Value("${variable.keycloak.client-secret}")
    private String clientSecret;

    private final Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);

    private HttpHeaders headers = new HttpHeaders();
    private MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    private HttpEntity<Object> httpEntity;

    @Autowired
    private RestTemplate restTemplate;
    
    public String getAccessToken() {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        
        httpEntity = new HttpEntity<>(body, headers);
        String URL = baseUrl + "/realms/" + realmName;
        String URL_GET_ACCESS = URL + "/protocol/openid-connect/token";
        String result = null;
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL_GET_ACCESS, httpEntity, String.class);
            LOGGER.info("access token - {}",response.getBody());
            result = response.getBody();
        } catch (HttpClientErrorException ex) {
            LOGGER.error("HTTP error occurred in KeycloakService: " + ex.getStatusCode() + " - " + ex.getStatusText());
            LOGGER.error("Response body: " + ex.getResponseBodyAsString());
        } catch (HttpServerErrorException ex) {
            LOGGER.error("HTTP server error occurred in KeycloakService: " + ex.getStatusCode() + " - " + ex.getStatusText());
            LOGGER.error("Response body: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            LOGGER.error("An error occurred in KeycloakService: " + ex.getMessage(), ex);
        }
        return result;
    }

}
