package com.example.SosLocaliza.gateways.dtos.request;

import com.example.SosLocaliza.domains.Evento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventoRequestDto {

    @NotBlank(message = "Nome do evento é obrigatório")
    @Size(max = 100, message = "Nome do evento deve ter no máximo 100 caracteres")
    private String nomeEvento;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricaoEvento;

    @Size(max = 300, message = "Causas devem ter no máximo 300 caracteres")
    private String causas;

    @Size(max = 300, message = "Alertas devem ter no máximo 300 caracteres")
    private String alertas;

    @Size(max = 500, message = "Ações antes devem ter no máximo 500 caracteres")
    private String acoesAntes;

    @Size(max = 500, message = "Ações durante devem ter no máximo 500 caracteres")
    private String acoesDurante;

    @Size(max = 500, message = "Ações depois devem ter no máximo 500 caracteres")
    private String acoesDepois;

    public Evento toEvento() {
        return Evento.builder()
                .nomeEvento(this.nomeEvento)
                .descricaoEvento(this.descricaoEvento)
                .causas(this.causas)
                .alertas(this.alertas)
                .acoesAntes(this.acoesAntes)
                .acoesDurante(this.acoesDurante)
                .acoesDepois(this.acoesDepois)
                .ativo(true)
                .build();
    }
}


