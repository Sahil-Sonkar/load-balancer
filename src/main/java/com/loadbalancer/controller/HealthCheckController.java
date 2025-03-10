package com.loadbalancer.controller;

import com.loadbalancer.service.HealthCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/health")
public class HealthCheckController {
    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> healthStatuses = healthCheckService.getServerHealthStatuses();
        if (healthStatuses.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "No backend servers registered"));
        }
        return ResponseEntity.ok(healthStatuses);
    }
}

