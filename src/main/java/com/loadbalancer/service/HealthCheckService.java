package com.loadbalancer.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthCheckService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final LoadBalancer loadBalancer;

    public HealthCheckService(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void performHealthCheck() {
        List<String> healthyServers = loadBalancer.getBackendServers().stream()
                .filter(this::isServerHealthy)
                .collect(Collectors.toList());
        loadBalancer.updateBackendServers(healthyServers);
    }

    private boolean isServerHealthy(String server) {
        try {
            restTemplate.getForEntity(server + "/health", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
