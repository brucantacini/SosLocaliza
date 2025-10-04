package com.example.SosLocaliza.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @GetMapping
    public String listarEventos() {
        return "Lista de eventos - em desenvolvimento";
    }

    @GetMapping("/{id}")
    public String buscarEventoPorId() {
        return "Buscar evento por ID - em desenvolvimento";
    }
}