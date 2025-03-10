package com.loadbalancer.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancerTest {

    @Mock
    private LoadBalancingStrategy strategy;

    private LoadBalancer loadBalancer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loadBalancer = new LoadBalancer(new ArrayList<>(List.of("http://localhost:8081", "http://localhost:8082")), strategy);
    }

    @Test
    void testSetStrategy() {
        LoadBalancingStrategy newStrategy = mock(LoadBalancingStrategy.class);
        loadBalancer.setStrategy(newStrategy);
        assertEquals(newStrategy.getClass().getSimpleName(), loadBalancer.getStrategyName());
    }

    @Test
    void testGetNextServer() {
        when(strategy.selectServer(anyList(), anyMap())).thenReturn("http://localhost:8081");
        String server = loadBalancer.getNextServer();
        assertEquals("http://localhost:8081", server);
    }

    @Test
    void testAddServer() {
        loadBalancer.addServer("http://localhost:8083");
        assertTrue(loadBalancer.getBackendServers().contains("http://localhost:8083"));
    }

    @Test
    void testAddServer_Duplicate() {
        assertThrows(IllegalStateException.class, () -> loadBalancer.addServer("http://localhost:8081"));
    }

    @Test
    void testRemoveServer() {
        loadBalancer.removeServer("http://localhost:8081");
        assertFalse(loadBalancer.getBackendServers().contains("http://localhost:8081"));
    }

    @Test
    void testRemoveServer_NotExist() {
        assertThrows(IllegalStateException.class, () -> loadBalancer.removeServer("http://localhost:9999"));
    }

    @Test
    void testUpdateBackendServers() {
        List<String> newServers = List.of("http://localhost:8083");
        loadBalancer.updateBackendServers(newServers);
        assertEquals(newServers, loadBalancer.getBackendServers());
    }
}

