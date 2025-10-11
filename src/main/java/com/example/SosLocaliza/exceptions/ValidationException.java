package com.example.SosLocaliza.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public ValidationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "VALIDATION_ERROR";
    }
}



