package com.loadbalancer.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class HealthCheckServiceTest {

    @Mock
    private LoadBalancer loadBalancer;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HealthCheckService healthCheckService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(healthCheckService, "restTemplate", restTemplate);
    }

    @Test
    void testPerformHealthCheck_WithHealthyServers() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082");
        when(loadBalancer.getBackendServers()).thenReturn(servers);
        when(restTemplate.getForEntity("http://localhost:8081/health", String.class)).thenReturn(ResponseEntity.ok("Healthy"));
        when(restTemplate.getForEntity("http://localhost:8082/health", String.class)).thenReturn(ResponseEntity.ok("Healthy"));

        healthCheckService.performHealthCheck();

        Map<String, String> healthStatuses = healthCheckService.getServerHealthStatuses();
        System.out.println("Health Statuses: " + healthStatuses);
        assertEquals("Healthy", healthStatuses.get("http://localhost:8081"));
        assertEquals("Healthy", healthStatuses.get("http://localhost:8082"));
    }

    @Test
    void testPerformHealthCheck_WithUnhealthyServer() {
        List<String> servers = List.of("http://localhost:8081");
        when(loadBalancer.getBackendServers()).thenReturn(servers);
        when(restTemplate.getForEntity("http://localhost:8081/health", String.class)).thenThrow(new RuntimeException("Connection failed"));

        healthCheckService.performHealthCheck();

        Map<String, String> healthStatuses = healthCheckService.getServerHealthStatuses();
        assertEquals("Unhealthy", healthStatuses.get("http://localhost:8081"));
    }

    @Test
    void testPerformHealthCheck_WithNoServers() {
        when(loadBalancer.getBackendServers()).thenReturn(List.of());

        healthCheckService.performHealthCheck();

        Map<String, String> healthStatuses = healthCheckService.getServerHealthStatuses();
        assertTrue(healthStatuses.isEmpty());
    }
}
