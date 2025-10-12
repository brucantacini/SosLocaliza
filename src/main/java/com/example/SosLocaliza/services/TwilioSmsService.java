package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.exceptions.SmsException;
import com.example.SosLocaliza.exceptions.ValidationException;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsService {

    private final SmsService smsService;

    public void inicializarTwilio() {
        log.info("SMS Service inicializado - Modo Simulação (Twilio removido)");
    }

    public SmsMessage enviarSmsViaTwilio(SmsRequestDto smsRequestDto) {
        inicializarTwilio();
        
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();
        
        try {
            // Validação do número de telefone
            if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
                throw new ValidationException("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
            }

            // Simular envio de SMS (sem Twilio)
            String mensagemCompleta = "De: " + smsRequestDto.getRemetente() + "\n" + smsRequestDto.getMensagem();
            
            // Simular delay de envio
            Thread.sleep(1000);
            
            // Marcar como enviado com sucesso
            smsMessage = smsMessage.withEnviadoComSucesso(true)
                                 .withErro(null);
            
            log.info("SMS SIMULADO enviado com sucesso para {}: {}", smsMessage.getNumeroTelefone(), "SIM" + System.currentTimeMillis());
            
        } catch (ValidationException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro geral ao simular SMS: {}", e.getMessage());
            throw new SmsException("Erro geral ao simular SMS: " + e.getMessage(), e);
        }

        return smsService.enviarSms(smsMessage);
    }

    public SmsMessage enviarSmsComEvento(SmsRequestDto smsRequestDto, Long idEvento) {
        inicializarTwilio();
        
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();
        
        try {
            // Validação do número de telefone
            if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
                smsMessage = smsMessage.withEnviadoComSucesso(false)
                                     .withErro("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
                log.error("Número de telefone inválido: {}", smsMessage.getNumeroTelefone());
                return smsService.enviarSmsComEvento(smsMessage, idEvento);
            }

            // Simular envio de SMS de emergência (sem Twilio)
            String mensagemCompleta = "ALERTA DE EMERGÊNCIA\n" +
                                    "De: " + smsRequestDto.getRemetente() + "\n" +
                                    "Evento: " + idEvento + "\n" +
                                    "Mensagem: " + smsRequestDto.getMensagem();
            
            // Simular delay de envio
            Thread.sleep(1500);
            
            // Marcar como enviado com sucesso
            smsMessage = smsMessage.withEnviadoComSucesso(true)
                                 .withErro(null);
            
            log.info("SMS de emergência SIMULADO enviado com sucesso para {}: {}", smsMessage.getNumeroTelefone(), "SIM" + System.currentTimeMillis());
            
        } catch (Exception e) {
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                                 .withErro("Erro geral: " + e.getMessage());
            log.error("Erro geral ao simular SMS de emergência: {}", e.getMessage());
        }

        return smsService.enviarSmsComEvento(smsMessage, idEvento);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Validação simplificada para debug
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // Verificar se começa com +55 e tem pelo menos 13 caracteres
        if (phoneNumber.startsWith("+55") && phoneNumber.length() >= 13) {
            return true;
        }
        
        log.warn("Número de telefone não válido: {}", phoneNumber);
        return false;
    }
}
