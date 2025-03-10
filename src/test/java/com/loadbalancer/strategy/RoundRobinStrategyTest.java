package com.loadbalancer.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class RoundRobinStrategyTest {

    private RoundRobinStrategy roundRobinStrategy;

    @BeforeEach
    void setUp() {
        roundRobinStrategy = new RoundRobinStrategy();
    }

    @Test
    void testSelectServer_WithMultipleServers() {
        List<String> servers = List.of("http://localhost:8081", "http://localhost:8082", "http://localhost:8083");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        String firstServer = roundRobinStrategy.selectServer(servers, serverConnections);
        String secondServer = roundRobinStrategy.selectServer(servers, serverConnections);
        String thirdServer = roundRobinStrategy.selectServer(servers, serverConnections);
        String fourthServer = roundRobinStrategy.selectServer(servers, serverConnections);

        assertEquals("http://localhost:8081", firstServer);
        assertEquals("http://localhost:8082", secondServer);
        assertEquals("http://localhost:8083", thirdServer);
        assertEquals("http://localhost:8081", fourthServer);
    }

    @Test
    void testSelectServer_WithSingleServer() {
        List<String> servers = List.of("http://localhost:8081");
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        String selectedServer = roundRobinStrategy.selectServer(servers, serverConnections);
        assertEquals("http://localhost:8081", selectedServer);
    }

    @Test
    void testSelectServer_WithNoServers() {
        List<String> servers = List.of();
        Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();

        Exception exception = assertThrows(RuntimeException.class, () ->
                roundRobinStrategy.selectServer(servers, serverConnections)
        );
        assertEquals("No backend servers available", exception.getMessage());
    }
}