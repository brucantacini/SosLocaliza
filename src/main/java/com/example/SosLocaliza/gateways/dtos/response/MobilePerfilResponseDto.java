package com.example.SosLocaliza.gateways.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MobilePerfilResponseDto {

    private final String username;
    private final String nomeExibicao;
    private final String localizacao;
    private final String ddd;
    private final String numeroLocal;
}
