package com.example.SosLocaliza.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @PostMapping
    public String enviarSms() {
        return "Enviar SMS - em desenvolvimento";
    }

    @GetMapping
    public String listarSms() {
        return "Lista de SMS - em desenvolvimento";
    }

    @GetMapping("/{id}")
    public String buscarSmsPorId() {
        return "Buscar SMS por ID - em desenvolvimento";
    }
