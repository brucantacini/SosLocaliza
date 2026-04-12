package com.example.SosLocaliza.gateways.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmsUpdateDto {

    @NotBlank(message = "Remetente é obrigatório")
    @Size(max = 100, message = "Remetente deve ter no máximo 100 caracteres")
    private String remetente;

    @NotBlank(message = "DDD é obrigatório")
    @Pattern(regexp = "\\d{2}", message = "DDD deve ter exatamente 2 dígitos")
    private String ddd;

    @NotBlank(message = "Número de telefone é obrigatório")
    @Pattern(regexp = "\\d{8,9}", message = "Número deve ter entre 8 e 9 dígitos")
    private String numeroTelefone;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 1000, message = "Mensagem deve ter no máximo 1000 caracteres")
    private String mensagem;

    @NotNull(message = "ID do evento é obrigatório")
    private Long idEvento;

    private Boolean enviadoComSucesso;

    @Size(max = 500, message = "Erro deve ter no máximo 500 caracteres")
    private String erro;
}
