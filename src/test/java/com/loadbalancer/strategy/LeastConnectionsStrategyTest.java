package com.loadbalancer.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class LeastConnectionsStrategyTest {

    private LeastConnectionsStrategy leastConnectionsStrategy;

    @BeforeEach
    void setUp() {
        leastConnectionsStrategy = new LeastConnectionsStrategy();
    }

    @Test
    void testSelectServer_WithMultipleServers() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082", "http://localhost:8083");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();
        serverConnections.put("http://localhost:8081", new AtomicInteger(3));
        serverConnections.put("http://localhost:8082", new AtomicInteger(1));
        serverConnections.put("http://localhost:8083", new AtomicInteger(5));

        String selectedServer = leastConnectionsStrategy.selectServer(servers, serverConnections);
        assertEquals("http://localhost:8082", selectedServer);
    }

    @Test
    void testSelectServer_WithEqualConnections() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();
        serverConnections.put("http://localhost:8081", new AtomicInteger(2));
        serverConnections.put("http://localhost:8082", new AtomicInteger(2));

        String selectedServer = leastConnectionsStrategy.selectServer(servers, serverConnections);
        assertTrue(servers.contains(selectedServer));
    }

    @Test
    void testSelectServer_WithNoServers() {
        List<String> servers = List.of();
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        Exception exception = assertThrows(RuntimeException.class, () ->
                leastConnectionsStrategy.selectServer(servers, serverConnections)
        );
        assertEquals("No backend servers available", exception.getMessage());
    }

    @Test
    void testSelectServer_WithNoConnectionsTracked() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        String selectedServer = leastConnectionsStrategy.selectServer(servers, serverConnections);
        assertTrue(servers.contains(selectedServer));
    }
}

