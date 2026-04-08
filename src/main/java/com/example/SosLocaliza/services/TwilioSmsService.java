package com.example.SosLocaliza.services;

import com.example.SosLocaliza.configurations.TwilioConfig;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.exceptions.SmsException;
import com.example.SosLocaliza.exceptions.TwilioException;
import com.example.SosLocaliza.exceptions.ValidationException;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsService {

    private final SmsService smsService;
    private final TwilioConfig twilioConfig;

    @Value("${twilio.trial-number:+15005550006}")
    private String trialNumber;

    public SmsMessage enviarSmsViaTwilio(SmsRequestDto smsRequestDto) {
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();
        
        try {
            if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
                throw new ValidationException("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
            }

            String mensagemCompleta = smsRequestDto.getRemetente() + ": " + smsRequestDto.getMensagem();

            if (twilioConfig.isEnabled()) {
                return enviarSmsReal(smsMessage, mensagemCompleta);
            } else {
                return enviarSmsSimulado(smsMessage, mensagemCompleta);
            }

        } catch (ValidationException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao enviar SMS: {}", e.getMessage());
            throw new SmsException("Erro ao enviar SMS: " + e.getMessage(), e);
        }
    }

    public SmsMessage enviarSmsComEvento(SmsRequestDto smsRequestDto, Long idEvento) {
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();
        
        try {
            if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
                smsMessage = smsMessage.withEnviadoComSucesso(false)
                                     .withErro("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
                log.error("Número de telefone inválido: {}", smsMessage.getNumeroTelefone());
                return smsService.enviarSmsComEvento(smsMessage, idEvento);
            }

            String mensagemCompleta = "🚨 ALERTA DE EMERGÊNCIA 🚨\n" +
                                    "De: " + smsRequestDto.getRemetente() + "\n" +
                                    "Evento ID: " + idEvento + "\n" +
                                    "Mensagem: " + smsRequestDto.getMensagem();

            if (twilioConfig.isEnabled()) {
                SmsMessage smsEnviado = enviarSmsRealSemPersistir(smsMessage, mensagemCompleta);
                return smsService.enviarSmsComEvento(smsEnviado, idEvento);
            } else {
                SmsMessage smsEnviado = enviarSmsSimuladoSemPersistir(smsMessage);
                return smsService.enviarSmsComEvento(smsEnviado, idEvento);
            }

        } catch (Exception e) {
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                                 .withErro("Erro geral: " + e.getMessage());
            log.error("Erro ao enviar SMS de emergência: {}", e.getMessage());
            return smsService.enviarSmsComEvento(smsMessage, idEvento);
        }
    }

    private SmsMessage enviarSmsReal(SmsMessage smsMessage, String mensagemCompleta) {
        try {
            log.info("📤 Enviando SMS REAL via Twilio para: {}", smsMessage.getNumeroTelefone());
            
            Message message = Message.creator(
                    new PhoneNumber(smsMessage.getNumeroTelefone()),
                    new PhoneNumber(trialNumber),
                    mensagemCompleta
            ).create();

            log.info("✅ SMS enviado com sucesso! SID: {}", message.getSid());
            
            smsMessage = smsMessage.withEnviadoComSucesso(true)
                                 .withErro(null);
            
            return smsService.enviarSms(smsMessage);
            
        } catch (Exception e) {
            log.error("❌ Erro ao enviar SMS via Twilio: {}", e.getMessage());
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                                 .withErro("Erro Twilio: " + e.getMessage());
            throw new TwilioException("Erro ao enviar SMS via Twilio: " + e.getMessage(), e);
        }
    }

    private SmsMessage enviarSmsSimulado(SmsMessage smsMessage, String mensagemCompleta) {
        try {
            log.info("📤 [SIMULAÇÃO] Enviando SMS simulado para: {}", smsMessage.getNumeroTelefone());
            Thread.sleep(1000);
            smsMessage = smsMessage.withEnviadoComSucesso(true).withErro(null);
            log.info("✅ [SIMULAÇÃO] SMS simulado enviado com sucesso!");
            return smsService.enviarSms(smsMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SmsException("Erro ao simular envio de SMS", e);
        }
    }

    private SmsMessage enviarSmsRealSemPersistir(SmsMessage smsMessage, String mensagemCompleta) {
        try {
            log.info("📤 Enviando SMS REAL via Twilio para: {}", smsMessage.getNumeroTelefone());

            Message message = Message.creator(
                    new PhoneNumber(smsMessage.getNumeroTelefone()),
                    new PhoneNumber(trialNumber),
                    mensagemCompleta
            ).create();

            log.info("✅ SMS enviado com sucesso! SID: {}", message.getSid());
            return smsMessage.withEnviadoComSucesso(true).withErro(null);
        } catch (Exception e) {
            log.error("❌ Erro ao enviar SMS via Twilio: {}", e.getMessage());
            throw new TwilioException("Erro ao enviar SMS via Twilio: " + e.getMessage(), e);
        }
    }

    private SmsMessage enviarSmsSimuladoSemPersistir(SmsMessage smsMessage) {
        try {
            log.info("📤 [SIMULAÇÃO] Enviando SMS simulado para: {}", smsMessage.getNumeroTelefone());
            Thread.sleep(1000);
            log.info("✅ [SIMULAÇÃO] SMS simulado enviado com sucesso!");
            return smsMessage.withEnviadoComSucesso(true).withErro(null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SmsException("Erro ao simular envio de SMS", e);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        if (phoneNumber.startsWith("+55") && phoneNumber.length() >= 13) {
            return true;
        }
        if (phoneNumber.startsWith("+") && phoneNumber.length() >= 10) {
            return true;
        }
        log.warn("Número de telefone não válido: {}", phoneNumber);
        return false;
    }
}
