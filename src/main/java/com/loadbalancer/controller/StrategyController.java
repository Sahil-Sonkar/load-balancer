package com.loadbalancer.controller;


import com.loadbalancer.service.LoadBalancer;
import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {
    private final LoadBalancer loadBalancer;
    private final Map<String, LoadBalancingStrategy> strategies;

    public StrategyController(LoadBalancer loadBalancer, List<LoadBalancingStrategy> strategyList) {
        this.loadBalancer = loadBalancer;
        this.strategies = new ConcurrentHashMap<>();
        // Populate the strategies map dynamically from Spring-managed beans
        for (LoadBalancingStrategy strategy : strategyList) {
            String key = strategy.getClass().getSimpleName().toLowerCase();
            strategies.put(key, strategy);
        }
    }

    @PostMapping("/change")
    public ResponseEntity<String> changeStrategy(@RequestBody Map<String, String> request) {
        String strategyName = request.get("strategy");
        if (strategyName == null || strategyName.isEmpty()) {
            return ResponseEntity.badRequest().body("Strategy name cannot be empty");
        }
        if (strategies.containsKey(strategyName)) {
            loadBalancer.setStrategy(strategies.get(strategyName));
            return ResponseEntity.ok("Strategy changed to: " + strategyName);
        }
        return ResponseEntity.badRequest().body("Invalid Strategy: " + strategyName);
    }

    @GetMapping("/current")
    public ResponseEntity<String> getCurrentStrategy() {
        return ResponseEntity.ok("Current Strategy: " + loadBalancer.getStrategyName());
    }
}