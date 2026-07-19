package com.brunopedraca.celcoin.banking;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

public final class AccountDtos {
    private AccountDtos() {}

    public record CelcoinAccountStatusResponse(String accountId, String status, Map<String, Object> raw) {}

    public record CelcoinAccountResponse(String accountId, String document, String type, String status, Map<String, Object> raw) {}

    public record CelcoinBalanceResponse(String accountId, BigDecimal available, BigDecimal blocked, String currency, Map<String, Object> raw) {}

    public record CelcoinStatementRequest(@NotBlank String accountId, LocalDate startDate, LocalDate endDate) {}

    public record CelcoinStatementResponse(String accountId, java.util.List<CelcoinStatementTransactionResponse> transactions, Map<String, Object> raw) {}

    public record CelcoinStatementTransactionResponse(
            String transactionId, OffsetDateTime createdAt, BigDecimal amount, String type, String status, Map<String, Object> raw) {}

    public record CelcoinInternalTransferRequest(
            @NotBlank String sourceAccountId, @NotBlank String targetAccountId, BigDecimal amount, String description) {}

    public record CelcoinInternalTransferResponse(String transferId, String status, Map<String, Object> raw) {}
}
