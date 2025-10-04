package com.example.SosLocaliza.dto;

import lombok.Data;

@Data
public class SmsRequest {
    
    private String sender;
    private Integer ddd;
    private String phoneNumber;
    private String message;
    private Long eventoId;
}