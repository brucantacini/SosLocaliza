package com.example.SosLocaliza.gateways.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiscoAreaMobileDto {

    private final double latitude;
    private final double longitude;
    private final String risco_previsto;
}
