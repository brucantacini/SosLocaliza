package com.example.SosLocaliza.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponseDto {
    
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private List<String> details;
    
    public static ErrorResponseDto of(String errorCode, String message, String path) {
        return ErrorResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    public static ErrorResponseDto of(String errorCode, String message, String path, List<String> details) {
        return ErrorResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .details(details)
                .build();
    }
}
