package com.loadbalancer.strategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface LoadBalancingStrategy {
    String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections);
}
