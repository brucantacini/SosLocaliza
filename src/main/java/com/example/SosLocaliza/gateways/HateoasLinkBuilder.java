package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.gateways.dtos.response.EventoResponseDto;
import com.example.SosLocaliza.gateways.dtos.response.SmsResponseDto;
import org.springframework.hateoas.Link;

public class HateoasLinkBuilder {

    public static void adicionarLinksEvento(EventoResponseDto eventoDto) {
        try {
            if (eventoDto.getIdEvento() == null) {
                return;
            }
            Long id = eventoDto.getIdEvento();
            eventoDto.add(Link.of("/api/eventos/getById/" + id).withSelfRel());
            eventoDto.add(Link.of("/api/eventos/getAll?page=0&size=10&direction=ASC").withRel("collection"));
        } catch (Exception e) {
        }
    }

    public static void adicionarLinksSms(SmsResponseDto smsDto) {
        try {
            if (smsDto.getNumeroTelefone() != null && !smsDto.getNumeroTelefone().isEmpty()) {
                String numeroEncoded = java.net.URLEncoder.encode(smsDto.getNumeroTelefone(), java.nio.charset.StandardCharsets.UTF_8);
                smsDto.add(Link.of("/api/sms/ultimoSms/" + numeroEncoded).withSelfRel());
            }
            
            smsDto.add(Link.of("/api/sms/getAll?page=0&size=10&direction=DESC").withRel("collection"));
        } catch (Exception e) {
        }
    }

}
