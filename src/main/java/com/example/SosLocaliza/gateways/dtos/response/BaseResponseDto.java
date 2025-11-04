package com.example.SosLocaliza.gateways.dtos.response;

import org.springframework.hateoas.RepresentationModel;

/**
 * Classe base para DTOs de resposta que suportam HATEOAS.
 * Facilita a adição de links nos DTOs seguindo o padrão do Spring HATEOAS.
 */
public abstract class BaseResponseDto<T extends BaseResponseDto<T>> extends RepresentationModel<T> {
    
}

