package com.example.SosLocaliza.dto;

import lombok.Data;

@Data
public class EventoDto {
    
    private Long idEvento;
    private String nomeEvento;
    private String descricaoEvento;
    private String causas;
    private String alertas;
    private String acoesAntes;
    private String acoesDurante;
    private String acoesDepois;
}