package com.loadbalancer.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class RandomSelectionStrategyTest {

    private RandomSelectionStrategy randomSelectionStrategy;

    @BeforeEach
    void setUp() {
        randomSelectionStrategy = new RandomSelectionStrategy();
    }

    @Test
    void testSelectServer_WithMultipleServers() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082", "http://localhost:8083");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        String selectedServer = randomSelectionStrategy.selectServer(servers, serverConnections);
        assertTrue(servers.contains(selectedServer));
    }

    @Test
    void testSelectServer_WithSingleServer() {
        List<String> servers = List.of("http://localhost:8081");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        String selectedServer = randomSelectionStrategy.selectServer(servers, serverConnections);
        assertEquals("http://localhost:8081", selectedServer);
    }

    @Test
    void testSelectServer_WithNoServers() {
        List<String> servers = List.of();
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        Exception exception = assertThrows(RuntimeException.class, () ->
                randomSelectionStrategy.selectServer(servers, serverConnections)
        );
        assertEquals("No backend servers available", exception.getMessage());
    }
}
