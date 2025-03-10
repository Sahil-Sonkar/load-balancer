package com.loadbalancer.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.loadbalancer.service.LoadBalancer;
import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrategyControllerTest {

    @Mock
    private LoadBalancer loadBalancer;

    @Mock
    private LoadBalancingStrategy roundRobinStrategy;

    @Mock
    private LoadBalancingStrategy leastConnectionsStrategy;

    @Mock
    private LoadBalancingStrategy randomSelectionStrategy;

    private StrategyController strategyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manually map strategy names based on known implementations
        Map<String, LoadBalancingStrategy> strategies = new ConcurrentHashMap<>();
        strategies.put("roundrobin", roundRobinStrategy);
        strategies.put("leastconnections", leastConnectionsStrategy);
        strategies.put("randomselection", randomSelectionStrategy);

        // Pass the correct strategy mapping
        strategyController = new StrategyController(loadBalancer, new ArrayList<>(List.of(roundRobinStrategy, leastConnectionsStrategy, randomSelectionStrategy)));
    }

    @Test
    void testChangeStrategy_Success() {
        // Arrange
        String strategyName = randomSelectionStrategy.getClass().getSimpleName().toLowerCase();
        Map<String, String> request = Map.of("strategy", strategyName);

        // Act
        ResponseEntity<String> response = strategyController.changeStrategy(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Strategy changed to: " + strategyName, response.getBody());
        verify(loadBalancer).setStrategy(randomSelectionStrategy); // Verify the correct strategy is set
    }

    @Test
    void testChangeStrategy_InvalidStrategy() {
        Map<String, String> request = Map.of("strategy", "invalidstrategy");
        ResponseEntity<String> response = strategyController.changeStrategy(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Strategy: invalidstrategy", response.getBody());
    }

    @Test
    void testChangeStrategy_EmptyStrategy() {
        Map<String, String> request = Map.of("strategy", "");
        ResponseEntity<String> response = strategyController.changeStrategy(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Strategy name cannot be empty", response.getBody());
    }

    @Test
    void testGetCurrentStrategy() {
        when(loadBalancer.getStrategyName()).thenReturn("RoundRobin");
        ResponseEntity<String> response = strategyController.getCurrentStrategy();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Current Strategy: RoundRobin", response.getBody());
    }
}
