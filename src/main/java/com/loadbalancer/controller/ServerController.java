package com.loadbalancer.controller;

import com.loadbalancer.service.LoadBalancer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/servers")
public class ServerController {
    private final LoadBalancer loadBalancer;

    public ServerController(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerServer(@RequestBody Map<String, String> request) {
        String serverUrl = request.get("serverUrl");
        if (serverUrl == null || serverUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Server URL cannot be empty");
        }
        loadBalancer.addServer(serverUrl);
        return ResponseEntity.ok("Server Registered: " + serverUrl);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeServer(@RequestBody Map<String, String> request) {
        String serverUrl = request.get("serverUrl");
        if (serverUrl == null || serverUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Server URL cannot be empty");
        }
        if (!loadBalancer.getBackendServers().contains(serverUrl)) {
            return ResponseEntity.badRequest().body("Server not found: " + serverUrl);
        }
        loadBalancer.removeServer(serverUrl);
        return ResponseEntity.ok("Server Removed: " + serverUrl);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listServers() {
        List<String> servers = loadBalancer.getBackendServers();
        if (servers.isEmpty()) {
            return ResponseEntity.ok(List.of("No servers registered"));
        }
        return ResponseEntity.ok(servers);
    }
}