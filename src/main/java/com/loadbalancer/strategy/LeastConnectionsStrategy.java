package com.loadbalancer.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeastConnectionsStrategy implements LoadBalancingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(LeastConnectionsStrategy.class);

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) {
            logger.error("No backend servers available for Least Connections selection");
            throw new RuntimeException("No backend servers available");
        }

        try {
            String selectedServer = servers.stream()
                    .min(Comparator.comparingInt(s -> serverConnections.getOrDefault(s, new AtomicInteger(0)).get()))
                    .orElseThrow(() -> new RuntimeException("Failed to select a server using Least Connections"));
            logger.info("Least Connections selected server: {}", selectedServer);
            return selectedServer;
        } catch (Exception e) {
            logger.error("Error selecting server using Least Connections: {}", e.getMessage(), e);
            throw new RuntimeException("Error selecting server using Least Connections", e);
        }
    }
}
