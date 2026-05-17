package com.example.SosLocaliza.gateways.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MobileCadastroRequestDto {

    @NotBlank
    @Size(max = 100)
    private String nomeCompleto;

    /**
     * CPF com ou sem máscara; o backend normaliza para 11 dígitos e grava em {@code USERNAME}.
     */
    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14, message = "CPF inválido")
    private String cpf;

    @NotBlank
    @Size(min = 6, max = 100)
    private String senha;

    /** Apenas dígitos: DDD + número (10 ou 11 dígitos), ex.: 11999999999 */
    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 10, max = 15, message = "Telefone deve ter DDD + número (10 a 11 dígitos)")
    private String telefone;

    /** Apenas dígitos do CEP (8). */
    @NotBlank(message = "CEP é obrigatório")
    @Size(min = 8, max = 9, message = "CEP deve ter 8 dígitos")
    private String cep;
}
