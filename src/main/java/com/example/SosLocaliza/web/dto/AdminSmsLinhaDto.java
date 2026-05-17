package com.example.SosLocaliza.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminSmsLinhaDto {

    private final LocalDateTime dataEnvio;
    private final String remetente;
    private final String telefone;
    private final String localizacao;
    private final String evento;
    private final String mensagem;
}
