package com.example.SosLocaliza.gateways.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SocorroForm {

    @NotNull(message = "Selecione o tipo de evento")
    private Long idEvento;

    @NotBlank(message = "Descreva a situação")
    @Size(max = 1000)
    private String mensagem;
}
