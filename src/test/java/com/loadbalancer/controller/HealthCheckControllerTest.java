package com.loadbalancer.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.loadbalancer.service.HealthCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;

public class HealthCheckControllerTest {

    @Mock
    private HealthCheckService healthCheckService;

    @InjectMocks
    private HealthCheckController healthCheckController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckHealth_WithHealthyServers() {
        Map<String, String> healthStatuses = new HashMap<>();
        healthStatuses.put("http://localhost:8081", "Healthy");
        healthStatuses.put("http://localhost:8082", "Healthy");
        when(healthCheckService.getServerHealthStatuses()).thenReturn(healthStatuses);

        ResponseEntity<Map<String, String>> response = healthCheckController.checkHealth();
        assertEquals(healthStatuses, response.getBody());
    }

    @Test
    void testCheckHealth_WithNoServers() {
        when(healthCheckService.getServerHealthStatuses()).thenReturn(Map.of());

        ResponseEntity<Map<String, String>> response = healthCheckController.checkHealth();
        assertEquals(Map.of("status", "No backend servers registered"), response.getBody());
    }
}
