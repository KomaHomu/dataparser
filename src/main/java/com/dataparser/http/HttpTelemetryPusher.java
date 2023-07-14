package com.dataparser.http;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpTelemetryPusher {
    private static final String API_URL = "http://localhost:8080/api/v1/{ACCESS_TOKEN}/telemetry";

    // TO-DO
    public void sendTelemetry(String accessToken, String volume) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = "{\"volume\":" + volume + "}";
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        String url = API_URL.replace("{ACCESS_TOKEN}", accessToken);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to push telemetry to Thingsboard: " + response.getBody());
        }
    }
}