package com.brunopedraca.celcoin.slc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class SlcDtos {
    private SlcDtos() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentInEvent(
            String entity, OffsetDateTime createTimestamp, String status, PaymentInBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentInBody(
            String id,
            String approvalId,
            String clientRequestId,
            String createDate,
            String lastUpdateDate,
            String movementType,
            String balanceType,
            BigDecimal oldBalance,
            BigDecimal currentBalance,
            BigDecimal amount,
            MovementAccount movementAccount,
            String description,
            List<Tag> tags) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovementAccount(String account, String taxId, String name, String branch) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tag(String key, String value) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SettlementErrorEvent(String entity, @JsonProperty("RequestBody") SettlementErrorBody requestBody) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SettlementErrorBody(String message) {}
}
