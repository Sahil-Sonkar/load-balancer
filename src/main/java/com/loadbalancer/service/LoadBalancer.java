package com.loadbalancer.service;

import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancer {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancer.class);
    private List<String> backendServers;
    private final Map<String, AtomicInteger> serverConnections = new ConcurrentHashMap<>();
    private LoadBalancingStrategy strategy;

    @Autowired
    public LoadBalancer(List<String> servers, LoadBalancingStrategy initialStrategy) {
        this.backendServers = servers;
        this.strategy = initialStrategy;
        servers.forEach(server -> serverConnections.put(server, new AtomicInteger(0)));
    }

    public synchronized void setStrategy(LoadBalancingStrategy newStrategy) {
        logger.info("Changing load balancing strategy to: {}", newStrategy.getClass().getSimpleName());
        this.strategy = newStrategy;
    }

    public String getNextServer() {
        try {
            String selectedServer = strategy.selectServer(backendServers, serverConnections);
            logger.info("Load balancer selected server: {}", selectedServer);
            return selectedServer;
        } catch (Exception e) {
            logger.error("Error selecting next server: {}", e.getMessage(), e);
            throw new RuntimeException("Error selecting next server", e);
        }
    }

    private boolean isValidUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public void addServer(String server) {
        if (server == null || server.isBlank() || !isValidUrl(server)) {
            logger.error("Invalid server URL provided: {}", server);
            throw new IllegalArgumentException("Invalid server URL");
        }
        if (backendServers.contains(server)) {
            logger.warn("Attempted to add duplicate server: {}", server);
            throw new IllegalStateException("Server is already registered");
        }

        logger.info("Adding backend server: {}", server);
        backendServers.add(server);
        serverConnections.put(server, new AtomicInteger(0));
    }

    public void removeServer(String server) {
        if (!backendServers.contains(server)) {
            logger.warn("Attempted to remove a non-existent server: {}", server);
            throw new IllegalStateException("Server not found");
        }

        logger.info("Removing backend server: {}", server);
        backendServers.remove(server);
        serverConnections.remove(server);
    }

    public synchronized List<String> getBackendServers() {
        return backendServers;
    }

    public synchronized void updateBackendServers(List<String> healthyServers) {
        logger.info("Updating backend servers with healthy ones: {}", healthyServers);
        this.backendServers = healthyServers;
    }

    public String getStrategyName() {
        return strategy.getClass().getSimpleName();
    }
}
