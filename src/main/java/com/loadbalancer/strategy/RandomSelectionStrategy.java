package com.loadbalancer.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RandomSelectionStrategy implements LoadBalancingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RandomSelectionStrategy.class);

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) {
            logger.error("No backend servers available for Random Selection");
            throw new RuntimeException("No backend servers available");
        }

        try {
            String selectedServer = servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
            logger.info("Random Selection chose server: {}", selectedServer);
            return selectedServer;
        } catch (Exception e) {
            logger.error("Error selecting server using Random Selection: {}", e.getMessage(), e);
            throw new RuntimeException("Error selecting server using Random Selection", e);
        }
    }
}
