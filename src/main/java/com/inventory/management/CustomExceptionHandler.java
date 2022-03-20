package com.inventory.management;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(InventoryException.class)
    public Mono<ResponseEntity<ValidationProblem>> handleInventoryException(InventoryException exception) {
        final ValidationProblem validationProblem = ValidationProblem.builder()
                .timestamp(Instant.now())
                .status(exception.getStatusCode().value())
                .error(exception.getStatusCode().getReasonPhrase())
                .message(exception.getStatusText())
                .build();
        switch (exception.getStatusCode()) {
            case NOT_FOUND:
                return Mono.fromCallable(() -> new ResponseEntity<>(validationProblem, HttpStatus.NOT_FOUND));
            case BAD_REQUEST:
                return Mono.fromCallable(() -> new ResponseEntity<>(validationProblem, HttpStatus.BAD_REQUEST));
            case FORBIDDEN:
                return Mono.fromCallable(() -> new ResponseEntity<>(validationProblem, HttpStatus.FORBIDDEN));
            default:
                return Mono.fromCallable(() -> new ResponseEntity<>(validationProblem, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
