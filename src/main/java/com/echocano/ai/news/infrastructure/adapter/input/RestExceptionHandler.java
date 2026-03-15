package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ServiceNotAvailableException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NotDefineException.class)
    public ResponseEntity<ErrorDetails> handleNotDefineException(
            NotDefineException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(
            Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        String cleanMessage = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String fieldName = "";
                    for (Path.Node node : violation.getPropertyPath()) {
                        fieldName = node.getName();
                    }
                    return String.format("Field '%s' %s", fieldName, violation.getMessage());
                })
                .collect(Collectors.joining("; "));

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed: " + cleanMessage,
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Schema(name = "ErrorResponse", description = "Standard error structure for API failures")
    public record ErrorDetails (
            @Schema(example = "2026-03-15T12:00:00") LocalDateTime timestamp,
            @Schema(example = "Service Unavailable") String message,
            @Schema(example = "uri=/api/v1/news") String description) {
    }
}
