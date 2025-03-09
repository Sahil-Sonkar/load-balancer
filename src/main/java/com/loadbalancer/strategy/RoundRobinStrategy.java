package com.loadbalancer.strategy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Primary  // This makes Round Robin the default strategy
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) throw new RuntimeException("No backend servers available");
        return servers.get(index.getAndUpdate(i -> (i + 1) % servers.size()));
    }
}
