package com.example.SosLocaliza.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EventoNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEventoNotFoundException(
            EventoNotFoundException ex, 
            HttpServletRequest request) {
        
        log.error("Evento não encontrado: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ErrorResponseDto> handleSmsException(
            SmsException ex, 
            HttpServletRequest request) {
        
        log.error("Erro no SMS: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(TwilioException.class)
    public ResponseEntity<ErrorResponseDto> handleTwilioException(
            TwilioException ex, 
            HttpServletRequest request) {
        
        log.error("Erro no Twilio: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        
        log.error("Erro de validação: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        log.error("Erro de validação Bean Validation: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                "VALIDATION_ERROR",
                "Erro de validação nos dados de entrada",
                request.getRequestURI(),
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {
        
        log.error("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponseDto error = ErrorResponseDto.of(
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        
        ErrorResponseDto error = ErrorResponseDto.of(
                "INTERNAL_SERVER_ERROR",
                "Erro interno do servidor. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
