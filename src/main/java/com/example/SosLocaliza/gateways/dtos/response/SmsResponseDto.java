package com.example.SosLocaliza.gateways.dtos.response;

import com.example.SosLocaliza.domains.SmsMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponseDto extends BaseResponseDto<SmsResponseDto> {
    
    private Long idSms;
    private String remetente;
    private String numeroTelefone;
    private String ddd;
    private String mensagem;
    private LocalDateTime dataEnvio;
    private Boolean enviadoComSucesso;
    private String erro;
    private Long idEvento;

    public static SmsResponseDto fromSmsMessage(SmsMessage smsMessage) {
        SmsResponseDto dto = new SmsResponseDto();
        dto.idSms = smsMessage.getIdSms();
        dto.remetente = smsMessage.getRemetente();
        dto.numeroTelefone = smsMessage.getNumeroTelefone();
        dto.ddd = smsMessage.getDdd();
        dto.mensagem = smsMessage.getMensagem();
        dto.dataEnvio = smsMessage.getDataEnvio();
        dto.enviadoComSucesso = smsMessage.getEnviadoComSucesso();
        dto.erro = smsMessage.getErro();
        dto.idEvento = smsMessage.getEvento() != null ? smsMessage.getEvento().getIdEvento() : null;
        return dto;
    }
}
