package com.example.SosLocaliza;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashVerificationTest {

    @Test
    void flywaySeedHashMatchesDemoPassword() {
        String hash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        assertTrue(new BCryptPasswordEncoder().matches("password", hash),
                "Atualize V2__seed_users.sql com hash gerado por BCryptPasswordEncoder.encode(\"password\")");
    }
}
