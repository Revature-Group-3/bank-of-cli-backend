package com.revature.bankofcli.exception;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.revature.bankofcli.dto.ApiError;

/**
 * Turns exceptions thrown from any controller into the contract's error JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Our own errors, each ApiException already carries its status and contract message
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        return build(ex.getStatus(), ex.getMessage());
    }

    // DTO validation failures (@NotNull, @Positive, @Max, @Size...), return the annotation's message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("The request could not be processed.");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    // Everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(Exception ex) {
        // Spring's own request errors (unknown URL, wrong HTTP method, ...) know their status.
        // Without this check, the catch-all would turn those into 500 “internal error”,
        // which would be misleading for the frontend teams while they’re testing
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            HttpStatus known = HttpStatus.resolve(status.value());
            String message = (known != null) ? known.getReasonPhrase() : "The request could not be processed.";
            return build(status, message);
        }

        // A real bug, log the details for us, send the contract's generic 500 to the client.
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred. Please try again later.");
    }

    // The contract’s format is "2024-06-01T00:00:00Z", so this cuts it to whole seconds
    private ResponseEntity<ApiError> build(HttpStatusCode status, String message) {
        String timestamp = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
        return ResponseEntity.status(status).body(new ApiError(status.value(), message, timestamp));
    }
}