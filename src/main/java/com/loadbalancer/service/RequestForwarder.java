package com.loadbalancer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestForwarder {
    private static final Logger logger = LoggerFactory.getLogger(RequestForwarder.class);
    private final RestTemplate restTemplate;
    private final LoadBalancer loadBalancer;

    public RequestForwarder(LoadBalancer loadBalancer, RestTemplate restTemplate) {
        this.loadBalancer = loadBalancer;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> forwardRequest(String path) {
        logger.info("Received request to forward path: {}", path);

        try {
            String backendUrl = loadBalancer.getNextServer();
            logger.info("Selected backend server: {}", backendUrl);

            if (!backendUrl.endsWith("/")) {
                backendUrl += "/"; // Ensure trailing slash for proper URL formatting
            }
            backendUrl += path;

            logger.info("Final forwarding URL: {}", backendUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(backendUrl, String.class);
            logger.info("Received response from backend: Status - {}, Body - {}", response.getStatusCode(), response.getBody());
            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            logger.error("Client error while forwarding request to {}: {}", path, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body("Client error: " + e.getMessage());

        } catch (ResourceAccessException e) {
            logger.error("Failed to connect to backend server while forwarding request to {}: {}", path, e.getMessage());
            return ResponseEntity.internalServerError().body("Backend server unreachable.");

        } catch (Exception e) {
            logger.error("Unexpected error while forwarding request to {}: {}", path, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to forward request.");
        }
    }
}
