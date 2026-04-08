-- Senha de demonstração para ambos: password (BCrypt)
INSERT INTO T_SOS_APP_USER (USERNAME, PASSWORD, ENABLED, ROLE) VALUES
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', TRUE, 'ROLE_ADMIN'),
('citizen', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', TRUE, 'ROLE_USER');
