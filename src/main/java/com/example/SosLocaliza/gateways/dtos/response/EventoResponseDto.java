package com.example.SosLocaliza.gateways.dtos.response;

import com.example.SosLocaliza.domains.Evento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDto extends BaseResponseDto<EventoResponseDto> {
    
    private Long idEvento;
    private String nomeEvento;
    private String descricaoEvento;
    private String causas;
    private String alertas;
    private String acoesAntes;
    private String acoesDurante;
    private String acoesDepois;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Boolean ativo;

    public static EventoResponseDto fromEvento(Evento evento) {
        EventoResponseDto dto = new EventoResponseDto();
        dto.idEvento = evento.getIdEvento();
        dto.nomeEvento = evento.getNomeEvento();
        dto.descricaoEvento = evento.getDescricaoEvento();
        dto.causas = evento.getCausas();
        dto.alertas = evento.getAlertas();
        dto.acoesAntes = evento.getAcoesAntes();
        dto.acoesDurante = evento.getAcoesDurante();
        dto.acoesDepois = evento.getAcoesDepois();
        dto.dataCriacao = evento.getDataCriacao();
        dto.dataAtualizacao = evento.getDataAtualizacao();
        dto.ativo = evento.getAtivo();
        return dto;
    }
}
