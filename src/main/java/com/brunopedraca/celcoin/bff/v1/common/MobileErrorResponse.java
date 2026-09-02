package com.brunopedraca.celcoin.bff.v1.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "MobileV1Error")
public record MobileErrorResponse(
        @Schema(example = "VALIDATION_ERROR") String code,
        @Schema(example = "The request contains invalid fields") String message,
        @Schema(example = "8c6bda4e-50b9-489a-88b6-727f8dd3fc6d") String correlationId,
        OffsetDateTime timestamp,
        List<FieldViolation> violations) {
    public record FieldViolation(String field, String message) {}
}
