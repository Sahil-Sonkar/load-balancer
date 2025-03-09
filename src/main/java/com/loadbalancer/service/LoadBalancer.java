package com.loadbalancer.service;

import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadBalancer {
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
        this.strategy = newStrategy;
    }

    public String getNextServer() {
        return strategy.selectServer(backendServers, serverConnections);
    }

    public void addServer(String server) {
        backendServers.add(server);
        serverConnections.put(server, new AtomicInteger(0));
    }

    public void removeServer(String server) {
        backendServers.remove(server);
        serverConnections.remove(server);
    }

    public synchronized List<String> getBackendServers() {
        return backendServers;
    }

    public synchronized void updateBackendServers(List<String> healthyServers) {
        this.backendServers = healthyServers;
    }

    public String getStrategyName() {
        return strategy.getClass().getSimpleName();
    }

}
