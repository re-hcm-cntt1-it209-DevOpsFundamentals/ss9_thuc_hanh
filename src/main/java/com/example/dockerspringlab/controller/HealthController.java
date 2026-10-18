package com.example.dockerspringlab.controller;

import com.example.dockerspringlab.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${spring.application.name:docker-spring-lab}")
    private String appName;

    @GetMapping
    public ResponseEntity<HealthResponse> checkHealth() {
        HealthResponse response = new HealthResponse(
                "UP 1",
                appName,
                System.getProperty("java.version"),
                LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
