package com.example.SosLocaliza.gateways.dtos.response;

import com.example.SosLocaliza.domains.SmsMessage;
import java.time.LocalDateTime;

public record SmsResponseDto(
        String idSms,
        String remetente,
        String numeroTelefone,
        String ddd,
        String numero,
        String mensagem,
        LocalDateTime dataEnvio,
        Boolean enviadoComSucesso,
        String erro,
        String idEvento
) {
    public static SmsResponseDto fromSmsMessage(SmsMessage smsMessage) {
        return new SmsResponseDto(
                smsMessage.getIdSms(),
                smsMessage.getRemetente(),
                smsMessage.getNumeroTelefone(),
                smsMessage.getDdd(),
                smsMessage.getNumero(),
                smsMessage.getMensagem(),
                smsMessage.getDataEnvio(),
                smsMessage.getEnviadoComSucesso(),
                smsMessage.getErro(),
                smsMessage.getEvento() != null ? smsMessage.getEvento().getIdEvento() : null
        );
    }
}
