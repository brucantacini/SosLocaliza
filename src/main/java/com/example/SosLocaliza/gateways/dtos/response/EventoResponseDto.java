package com.example.SosLocaliza.gateways.dtos.response;

import com.example.SosLocaliza.domains.Evento;
import java.time.LocalDateTime;

public record EventoResponseDto(
        String idEvento,
        String nomeEvento,
        String descricaoEvento,
        String causas,
        String alertas,
        String acoesAntes,
        String acoesDurante,
        String acoesDepois,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        Boolean ativo
) {
    public static EventoResponseDto fromEvento(Evento evento) {
        return new EventoResponseDto(
                evento.getIdEvento(),
                evento.getNomeEvento(),
                evento.getDescricaoEvento(),
                evento.getCausas(),
                evento.getAlertas(),
                evento.getAcoesAntes(),
                evento.getAcoesDurante(),
                evento.getAcoesDepois(),
                evento.getDataCriacao(),
                evento.getDataAtualizacao(),
                evento.getAtivo()
        );
    }
}
