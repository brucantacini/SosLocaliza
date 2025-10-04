package com.example.SosLocaliza.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "message", "Aplicação funcionando normalmente"
        );
    }

    @GetMapping("/info")
    public Map<String, Object> applicationInfo() {
        return Map.of(
                "application", "SOS Localiza Spring",
                "version", "1.0.0",
                "description", "Sistema de resposta rápida para situações de risco climático",
                "framework", "Spring Boot 3.2.0",
                "java", System.getProperty("java.version"),
                "timestamp", LocalDateTime.now()
        );
    }
}