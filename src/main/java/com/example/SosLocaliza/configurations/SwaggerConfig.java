package com.example.SosLocaliza.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOS Localiza API")
                        .description("Sistema de emergência para situações climáticas extremas (enchentes, deslizamentos) que permite envio de SMS de socorro via Twilio e gestão de informações sobre eventos de risco.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe SOS Localiza")
                                .email("soslocaliza@fiap.com.br")
                                .url("https://github.com/brucantacini/SosLocaliza"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://soslocaliza.fiap.com.br/api")
                                .description("Servidor de Produção")
                ));
    }
}
