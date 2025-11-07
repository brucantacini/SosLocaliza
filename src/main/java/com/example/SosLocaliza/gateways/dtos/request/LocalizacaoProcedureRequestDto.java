package com.example.SosLocaliza.gateways.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocalizacaoProcedureRequestDto {
    
    @NotBlank(message = "Nome do local é obrigatório")
    @Size(max = 100, message = "Nome do local deve ter no máximo 100 caracteres")
    private String nomeLocal;
    
    @Size(max = 50, message = "Rua deve ter no máximo 50 caracteres")
    private String ruaLocal;
    
    @Positive(message = "Número do local deve ser maior que zero")
    private Integer numeroLocal;
    
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cepLocal;
}

