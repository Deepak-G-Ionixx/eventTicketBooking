package com.ionixx.eventticketbooking.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ionixx.eventticketbooking.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, List<String>> fieldErrors = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.computeIfAbsent(error.getField(), ignored -> new ArrayList<>())
                .add(error.getDefaultMessage())
        );

        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "One or more request fields are invalid",
            fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST_BODY",
            "Request body is missing, malformed, or contains an invalid value",
            Map.of()
        );
    }

    @ExceptionHandler(InvalidEventIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEventId(InvalidEventIdException exception) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_EVENT_ID",
            exception.getMessage(),
            Map.of()
        );
    }

    @ExceptionHandler(DuplicateEventException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEvent(DuplicateEventException exception) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "DUPLICATE_EVENT",
            exception.getMessage(),
            Map.of()
        );
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEventNotFound(EventNotFoundException exception) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "EVENT_NOT_FOUND",
            exception.getMessage(),
            Map.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST",
            exception.getMessage(),
            Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected error while processing request", exception);
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            Map.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
        HttpStatus status,
        String error,
        String message,
        Map<String, List<String>> fieldErrors
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            error,
            message,
            fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}
