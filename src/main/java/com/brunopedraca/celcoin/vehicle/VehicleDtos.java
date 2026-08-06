package com.brunopedraca.celcoin.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class VehicleDtos {
    private VehicleDtos() {}

    public record CelcoinVehicleDebtConsultRequest(
            @NotBlank String state,
            @NotBlank String licensePlate,
            @NotBlank String renavam,
            @NotBlank String documentNumber,
            @NotBlank String clientRequestId) {}

    public record CelcoinVehicleDebtConsultResponse(
            String version,
            String status,
            String transactionId,
            String message,
            String errorCode,
            CelcoinVehicleDebtConsultBody body,
            Map<String, Object> error) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinVehicleDebtConsultBody(
            String account,
            String clientRequestId,
            List<CelcoinVehicleDebt> debtList,
            Map<String, Object> vehicle) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinVehicleDebt(
            String debtId,
            String id,
            BigDecimal amount,
            String title,
            String description,
            String ait,
            String dueDate,
            String expirationDate,
            Boolean hasDiscount,
            Boolean isExpired,
            String type,
            Integer year,
            Boolean required,
            List<String> dependsOn,
            List<String> distinct,
            Map<String, Object> metaDetran) {}

    public record CelcoinVehicleDebtPaymentRequest(
            @NotBlank String account,
            @NotBlank String clientRequestId,
            @NotBlank String idConsult,
            List<@NotBlank String> debtIdList,
            List<Map<String, Object>> tags) {}

    public record CelcoinVehicleDebtResponse(
            String version, String status, Map<String, Object> body, Map<String, Object> error) {}
}
