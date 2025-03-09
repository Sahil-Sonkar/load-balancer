package com.loadbalancer.strategy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RandomSelectionStrategy implements LoadBalancingStrategy {

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) throw new RuntimeException("No backend servers available");
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }
}
