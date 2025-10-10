package com.example.SosLocaliza.services;

import com.example.SosLocaliza.domains.Evento;
import com.example.SosLocaliza.domains.SmsMessage;
import com.example.SosLocaliza.exceptions.SmsException;
import com.example.SosLocaliza.exceptions.TwilioException;
import com.example.SosLocaliza.exceptions.ValidationException;
import com.example.SosLocaliza.gateways.dtos.request.SmsRequestDto;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
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

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.trial-number}")
    private String trialNumber;

    private final SmsService smsService;

    public void inicializarTwilio() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio inicializado com sucesso");
    }

    public SmsMessage enviarSmsViaTwilio(SmsRequestDto smsRequestDto) {
        inicializarTwilio();
        
        SmsMessage smsMessage = smsRequestDto.toSmsMessage();
        
        try {
            // Validação do número de telefone
            if (!isValidPhoneNumber(smsMessage.getNumeroTelefone())) {
                throw new ValidationException("Número de telefone inválido: " + smsMessage.getNumeroTelefone());
            }

            // Preparar mensagem
            String mensagemCompleta = "De: " + smsRequestDto.getRemetente() + "\n" + smsRequestDto.getMensagem();
            
            // Enviar SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(smsMessage.getNumeroTelefone()),
                    new PhoneNumber(trialNumber),
                    mensagemCompleta
            ).create();

            // Marcar como enviado com sucesso
            smsMessage = smsMessage.withEnviadoComSucesso(true)
                                 .withErro(null);
            
            log.info("SMS enviado com sucesso para {}: {}", smsMessage.getNumeroTelefone(), message.getSid());
            
        } catch (ApiException e) {
            log.error("Erro ao enviar SMS via Twilio: {}", e.getMessage());
            throw new TwilioException("Erro ao enviar SMS via Twilio: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro geral ao enviar SMS: {}", e.getMessage());
            throw new SmsException("Erro geral ao enviar SMS: " + e.getMessage(), e);
        }

        return smsService.enviarSms(smsMessage);
    }

    public SmsMessage enviarSmsComEvento(SmsRequestDto smsRequestDto, String idEvento) {
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

            // Preparar mensagem com informações do evento
            String mensagemCompleta = "ALERTA DE EMERGÊNCIA\n" +
                                    "De: " + smsRequestDto.getRemetente() + "\n" +
                                    "Evento: " + idEvento + "\n" +
                                    "Mensagem: " + smsRequestDto.getMensagem();
            
            // Enviar SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(smsMessage.getNumeroTelefone()),
                    new PhoneNumber(trialNumber),
                    mensagemCompleta
            ).create();

            // Marcar como enviado com sucesso
            smsMessage = smsMessage.withEnviadoComSucesso(true)
                                 .withErro(null);
            
            log.info("SMS de emergência enviado com sucesso para {}: {}", smsMessage.getNumeroTelefone(), message.getSid());
            
        } catch (ApiException e) {
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                                 .withErro("Erro Twilio: " + e.getMessage());
            log.error("Erro ao enviar SMS de emergência via Twilio: {}", e.getMessage());
        } catch (Exception e) {
            smsMessage = smsMessage.withEnviadoComSucesso(false)
                                 .withErro("Erro geral: " + e.getMessage());
            log.error("Erro geral ao enviar SMS de emergência: {}", e.getMessage());
        }

        return smsService.enviarSmsComEvento(smsMessage, idEvento);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, "BR");
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            log.error("Erro ao validar número de telefone: {}", e.getMessage());
            return false;
        }
    }
}
