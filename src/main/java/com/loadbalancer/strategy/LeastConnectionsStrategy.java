package com.loadbalancer.strategy;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeastConnectionsStrategy implements LoadBalancingStrategy {

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) throw new RuntimeException("No backend servers available");
        return servers.stream()
                .min(Comparator.comparingInt(s -> serverConnections.getOrDefault(s, new AtomicInteger(0)).get()))
                .orElseThrow();
    }
}
