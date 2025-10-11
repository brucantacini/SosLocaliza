package com.example.SosLocaliza.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SmsException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public SmsException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "SMS_ERROR";
    }
    
    public SmsException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "SMS_ERROR";
    }
    
    public SmsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "SMS_ERROR";
    }
}


