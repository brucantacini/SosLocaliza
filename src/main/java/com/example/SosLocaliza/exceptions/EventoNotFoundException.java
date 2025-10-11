package com.example.SosLocaliza.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventoNotFoundException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public EventoNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = "EVENTO_NOT_FOUND";
    }
    
    public EventoNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = "EVENTO_NOT_FOUND";
    }
}


