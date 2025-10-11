package com.example.SosLocaliza.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TwilioException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public TwilioException(String message) {
        super(message);
        this.status = HttpStatus.SERVICE_UNAVAILABLE;
        this.errorCode = "TWILIO_ERROR";
    }
    
    public TwilioException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.SERVICE_UNAVAILABLE;
        this.errorCode = "TWILIO_ERROR";
    }
    
    public TwilioException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "TWILIO_ERROR";
    }
}


