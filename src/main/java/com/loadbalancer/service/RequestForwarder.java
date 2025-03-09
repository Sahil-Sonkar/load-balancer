package com.loadbalancer.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestForwarder {
    private final RestTemplate restTemplate;
    private final LoadBalancer loadBalancer;

    public RequestForwarder(LoadBalancer loadBalancer, RestTemplate restTemplate) {
        this.loadBalancer = loadBalancer;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> forwardRequest(String path) {
        String backendUrl = loadBalancer.getNextServer() + path;
        return restTemplate.getForEntity(backendUrl, String.class);
    }
}
