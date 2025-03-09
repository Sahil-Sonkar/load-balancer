package com.loadbalancer.controller;

import com.loadbalancer.service.RequestForwarder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoadBalancerController {
    private final RequestForwarder requestForwarder;

    public LoadBalancerController(RequestForwarder requestForwarder) {
        this.requestForwarder = requestForwarder;
    }

    @GetMapping("/forward")
    public ResponseEntity<String> forwardRequest(@RequestParam String path) {
        if (path == null || path.isEmpty()) {
            return ResponseEntity.badRequest().body("Path cannot be empty");
        }
        return requestForwarder.forwardRequest(path);
    }
}
