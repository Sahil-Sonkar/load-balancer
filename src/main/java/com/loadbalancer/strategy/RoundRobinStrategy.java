package com.loadbalancer.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Primary  // This makes Round Robin the default strategy
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RoundRobinStrategy.class);
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String selectServer(List<String> servers, Map<String, AtomicInteger> serverConnections) {
        if (servers.isEmpty()) {
            logger.error("No backend servers available for Round Robin selection");
            throw new RuntimeException("No backend servers available");
        }

        try {
            int selectedIndex = index.getAndUpdate(i -> (i + 1) % servers.size());
            String selectedServer = servers.get(selectedIndex);
            logger.info("Round Robin selected server: {}", selectedServer);
            return selectedServer;
        } catch (Exception e) {
            logger.error("Error selecting server using Round Robin: {}", e.getMessage(), e);
            throw new RuntimeException("Error selecting server using Round Robin", e);
        }
    }
}

