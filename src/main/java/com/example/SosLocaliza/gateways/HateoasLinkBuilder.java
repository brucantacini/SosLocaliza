package com.example.SosLocaliza.gateways;

import com.example.SosLocaliza.gateways.dtos.response.EventoResponseDto;
import com.example.SosLocaliza.gateways.dtos.response.SmsResponseDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

/**
 * Classe utilitária para facilitar a criação de links HATEOAS.
 * Usa WebMvcLinkBuilder do Spring HATEOAS para gerar links relativos.
 */
public class HateoasLinkBuilder {

    // Links para Eventos
    public static void adicionarLinksEvento(EventoResponseDto eventoDto) {
        Long id = eventoDto.getIdEvento();
        
        eventoDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EventoController.class)
                .buscarEventoPorId(id)).withSelfRel());
        
        eventoDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EventoController.class)
                .listarEventos(0, org.springframework.data.domain.Sort.Direction.ASC, 10, null, true))
                .withRel("collection"));
        
        eventoDto.add(WebMvcLinkBuilder.linkTo(EventoController.class)
                .slash("update").slash(id).withRel("update"));
        
        eventoDto.add(WebMvcLinkBuilder.linkTo(EventoController.class)
                .slash("delete").slash(id).withRel("delete"));
        
        eventoDto.add(WebMvcLinkBuilder.linkTo(EventoController.class)
                .slash("desativar").slash(id).withRel("desativar"));
        
        if (id != null) {
            eventoDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SmsController.class)
                    .buscarSmsPorEvento(id)).withRel("sms"));
        }
    }

    // Links para SMS
    public static void adicionarLinksSms(SmsResponseDto smsDto) {
        if (smsDto.getNumeroTelefone() != null) {
            smsDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SmsController.class)
                    .buscarUltimoSmsPorNumero(smsDto.getNumeroTelefone())).withSelfRel());
        }
        
        smsDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SmsController.class)
                .listarSms(0, org.springframework.data.domain.Sort.Direction.DESC, 10, null))
                .withRel("collection"));
        
        if (smsDto.getIdEvento() != null) {
            smsDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EventoController.class)
                    .buscarEventoPorId(smsDto.getIdEvento())).withRel("evento"));
        }
    }

    // Link para coleção de eventos
    public static Link linkParaColecaoEventos() {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EventoController.class)
                .listarEventos(0, org.springframework.data.domain.Sort.Direction.ASC, 10, null, true))
                .withRel("collection");
    }

    // Link para coleção de SMS
    public static Link linkParaColecaoSms() {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SmsController.class)
                .listarSms(0, org.springframework.data.domain.Sort.Direction.DESC, 10, null))
                .withRel("collection");
    }
}

