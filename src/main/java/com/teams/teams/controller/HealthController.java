package com.teams.teams.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Teams Platform");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Teams Collaboration Platform");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("description", "Microsoft Teams-like collaboration platform");
        response.put("api_version", "v1");
        return ResponseEntity.ok(response);
    }
}

