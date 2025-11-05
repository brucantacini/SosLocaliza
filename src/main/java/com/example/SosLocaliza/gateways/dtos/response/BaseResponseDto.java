package com.example.SosLocaliza.gateways.dtos.response;

import org.springframework.hateoas.RepresentationModel;

public abstract class BaseResponseDto<T extends BaseResponseDto<T>> extends RepresentationModel<T> {
}