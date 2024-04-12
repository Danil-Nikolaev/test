package com.example.stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class KeycloakConfig {

    @Value("${variable.keycloak.username}")
    private String username;

    @Value("${variable.keycloak.password}")
    private String password;

    @Value("${variable.keycloak.client-id}")
    private String clientId;

    @Value("${variable.keycloak.client-secret}")
    private String clientSecret;

    public HttpEntity<Object> getHttpEntity() {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        HttpEntity<Object> httpEntity;

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);

        httpEntity = new HttpEntity<>(body, headers);

        return httpEntity;

    }
}
