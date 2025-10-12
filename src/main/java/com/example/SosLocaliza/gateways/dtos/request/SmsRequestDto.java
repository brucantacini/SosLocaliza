package com.example.SosLocaliza.gateways.dtos.request;

import com.example.SosLocaliza.domains.SmsMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmsRequestDto {

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

    private Long idEvento;

    public SmsMessage toSmsMessage() {
        String numeroCompleto = "+55" + this.ddd + this.numeroTelefone;
        
        return SmsMessage.builder()
                .remetente(this.remetente)
                .ddd(this.ddd)
                .numeroTelefone(numeroCompleto)
                .mensagem(this.mensagem)
                .enviadoComSucesso(false)
                .build();
    }
}


