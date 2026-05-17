package com.example.SosLocaliza.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SmsNotFoundException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public SmsNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.errorCode = "SMS_NOT_FOUND";
    }
}
