package com.brunopedraca.celcoin.bff.v1.common;

import com.brunopedraca.celcoin.bff.correlation.CorrelationIdFilter;
import com.brunopedraca.celcoin.common.exception.CelcoinException;
import com.brunopedraca.celcoin.bff.v1.identity.MobileUnauthorizedException;
import com.brunopedraca.celcoin.bff.v1.identity.MobileForbiddenException;
import com.brunopedraca.celcoin.bff.v1.identity.MobileStepUpRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.brunopedraca.celcoin.bff.v1")
public class MobileExceptionHandler {
    @ExceptionHandler(MobileUnauthorizedException.class)
    ResponseEntity<MobileErrorResponse> unauthorized(MobileUnauthorizedException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication is required", request, List.of());
    }

    @ExceptionHandler(MobileForbiddenException.class)
    ResponseEntity<MobileErrorResponse> forbidden(MobileForbiddenException exception, HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN, "FORBIDDEN", "You are not allowed to access this resource", request, List.of());
    }

    @ExceptionHandler(MobileStepUpRequiredException.class)
    ResponseEntity<MobileErrorResponse> stepUp(MobileStepUpRequiredException exception, HttpServletRequest request) {
        return response(HttpStatus.PRECONDITION_REQUIRED, "STEP_UP_REQUIRED", "Step-up authentication is required", request, List.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<MobileErrorResponse> validation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<MobileErrorResponse.FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new MobileErrorResponse.FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        return response(
                HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "The request contains invalid fields", request, violations);
    }

    @ExceptionHandler(CelcoinException.class)
    ResponseEntity<MobileErrorResponse> celcoin(CelcoinException exception, HttpServletRequest request) {
        HttpStatusCode status = exception.status() == null ? HttpStatus.BAD_GATEWAY : exception.status();
        return response(status, "UPSTREAM_ERROR", "Unable to process the request", request, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<MobileErrorResponse> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<MobileErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", request, List.of());
    }

    private ResponseEntity<MobileErrorResponse> response(
            HttpStatusCode status,
            String code,
            String message,
            HttpServletRequest request,
            List<MobileErrorResponse.FieldViolation> violations) {
        return ResponseEntity.status(status)
                .body(new MobileErrorResponse(
                        code,
                        message,
                        request.getHeader(CorrelationIdFilter.HEADER),
                        OffsetDateTime.now(),
                        violations));
    }
}
