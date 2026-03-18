package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestControllerAdvice
public class RestExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ServiceNotAvailableException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                getTraceId(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NotDefineException.class)
    public ResponseEntity<ErrorDetails> handleNotDefineException(
            NotDefineException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                getTraceId(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(
            Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                getTraceId(),
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
                getTraceId(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private String getTraceId() {
        String traceId;
        if ((tracer.currentSpan() != null)) {
            traceId = Objects.requireNonNull(tracer.currentSpan()).context().traceId();
        } else {
            traceId = "none";
        }
        return traceId;
    }

    @Schema(name = "ErrorResponse", description = "Standard error structure for API failures")
    public record ErrorDetails (
            @Schema(example = "2026-03-15T12:00:00") LocalDateTime timestamp,
            @Schema(example = "Service Unavailable") String message,
            @Schema(example = "5f3b2a1c6e7d8f90") String traceId,
            @Schema(example = "uri=/api/v1/news") String description) {
    }
}
