package com.example.SosLocaliza.gateways.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureResponseDto {
    private Long id;
    private String mensagem;
    private Boolean sucesso;
    
    public static ProcedureResponseDto sucesso(Long id, String mensagem) {
        return ProcedureResponseDto.builder()
                .id(id)
                .mensagem(mensagem)
                .sucesso(true)
                .build();
    }
    
    public static ProcedureResponseDto erro(String mensagem) {
        return ProcedureResponseDto.builder()
                .mensagem(mensagem)
                .sucesso(false)
                .build();
    }
}

