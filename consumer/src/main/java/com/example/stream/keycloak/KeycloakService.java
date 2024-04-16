package com.example.stream.keycloak;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeycloakService {

    private KeycloakConfig keycloakConfig;
    private RestTemplate restTemplate;
    private String accessUrl;
    private HttpEntity<Object> httpEntity; 
    private AccessToken accessToken;
    private Instant timeGetAccessToken;
    private final Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);

    public KeycloakService(KeycloakConfig keycloakConfig, RestTemplate restTemplate, @Value("${variable.keycloak.access-url}") String accessUrl) {
        this.keycloakConfig = keycloakConfig;
        this.restTemplate = restTemplate;
        this.accessUrl = accessUrl;
        this.httpEntity = this.keycloakConfig.getHttpEntity();
    }

    public String getAccessToken() {
        if (checkAccesToken()) return accessToken.getAccessToken();
       
        LOGGER.info("update access token");
        ResponseEntity<AccessToken> response = restTemplate.postForEntity(accessUrl, httpEntity, AccessToken.class);
        timeGetAccessToken = Instant.now();
        accessToken = response.getBody();
        LOGGER.info("get access token"); 
        
        return response.getBody().getAccessToken();
    }

    public boolean checkAccesToken() {
        if (accessToken == null) return false;   
        
        Instant currentTime = Instant.now();
        if (timeGetAccessToken.until(currentTime, ChronoUnit.SECONDS) > accessToken.getExpiresIn()) return false;
    
        LOGGER.info("access token is valid");
        return true;
    }

}
