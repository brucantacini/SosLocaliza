package com.example.SosLocaliza.configurations;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.enabled:false}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled && !accountSid.isEmpty() && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("✅ Twilio inicializado com sucesso!");
            log.info("Account SID: {}", accountSid.substring(0, Math.min(8, accountSid.length())) + "...");
        } else {
            log.warn("⚠️ Twilio desabilitado. Usando modo simulação.");
            log.info("Para ativar Twilio, configure as variáveis de ambiente:");
            log.info("  - TWILIO_ACCOUNT_SID");
            log.info("  - TWILIO_AUTH_TOKEN");
            log.info("  - TWILIO_ENABLED=true");
        }
    }

    public boolean isEnabled() {
        return enabled && !accountSid.isEmpty() && !authToken.isEmpty();
    }
}











