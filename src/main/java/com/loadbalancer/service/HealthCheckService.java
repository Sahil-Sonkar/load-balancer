package com.loadbalancer.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HealthCheckService {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final LoadBalancer loadBalancer;
    @Getter
    private final Map<String, String> serverHealthStatuses = new ConcurrentHashMap<>();

    public HealthCheckService(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Scheduled(fixedRate = 10000) // ✅ Runs every 10 seconds
    public void performHealthCheck() {
        logger.info("Executing Health Check...");

        List<String> servers = loadBalancer.getBackendServers();
        logger.info("Checking servers: {}", servers);

        if (servers.isEmpty()) {
            logger.warn("No backend servers registered.");
            return;
        }

        for (String server : servers) {
            boolean isHealthy = checkServerHealth(server);
            serverHealthStatuses.put(server, isHealthy ? "Healthy" : "Unhealthy");
            logger.info("Server {} is {}", server, isHealthy ? "Healthy" : "Unhealthy");
        }
    }

    private boolean checkServerHealth(String server) {
        try {
            restTemplate.getForEntity(server + "/health", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
