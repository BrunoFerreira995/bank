package com.brunopedraca.celcoin.banking;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class AccountDtos {
    private AccountDtos() {}

    public record CelcoinAccountStatusResponse(String accountId, String status, Map<String, Object> raw) {}

    public record CelcoinAccountResponse(String accountId, String document, String type, String status, Map<String, Object> raw) {}

    public record CelcoinCoreAccountRequest(
            @NotBlank String document,
            @NotBlank String name,
            String type,
            String email,
            String phone,
            CelcoinAccountAddress address,
            Map<String, Object> metadata) {}

    public record CelcoinAccountAddress(
            String street, String number, String complement, String neighborhood, String city, String state, String postalCode, String country) {}

    public record CelcoinAccountFinancialInformation(
            BigDecimal monthlyIncome,
            BigDecimal monthlyRevenue,
            BigDecimal declaredAssets,
            String occupation,
            String businessActivity,
            String sourceOfFunds,
            Boolean politicallyExposedPerson) {}

    public record CelcoinAccountFinancialInformationRequest(
            @NotBlank String accountId, CelcoinAccountFinancialInformation financialInformation) {}

    public record CelcoinAccountCustomerUpdateRequest(
            @NotBlank String accountId,
            String name,
            String email,
            String phone,
            CelcoinAccountAddress address,
            Map<String, Object> metadata) {}

    public record CelcoinAccountClosureRequest(@NotBlank String accountId, String reason, Map<String, Object> metadata) {}

    public record CelcoinAccountListRequest(String document, String type, String status, Integer page, Integer size) {}

    public record CelcoinAccountListResponse(List<CelcoinAccountResponse> accounts, Integer page, Integer size, Long total, Map<String, Object> raw) {}

    public record CelcoinAccountCountResponse(Long total, Map<String, Object> raw) {}

    public record CelcoinJudicialBlockRequest(@NotBlank String accountId, BigDecimal amount, String processNumber, String reason) {}

    public record CelcoinJudicialBlockResponse(String blockId, String accountId, BigDecimal amount, String status, Map<String, Object> raw) {}

    public record CelcoinAccountStatusUpdateRequest(@NotBlank String accountId, @NotBlank String status, String reason) {}

    public record CelcoinSandboxBalanceRequest(@NotBlank String accountId, BigDecimal amount, String description) {}

    public record CelcoinAccountMonitoringRequest(@NotBlank String accountId, String type, Map<String, Object> metadata) {}

    public record CelcoinAccountMonitoringResponse(String monitoringId, String accountId, String status, Map<String, Object> raw) {}

    public record CelcoinAccountMonitoringWebhookEvent(
            String eventId,
            String eventType,
            String monitoringId,
            String accountId,
            String status,
            OffsetDateTime occurredAt,
            Map<String, Object> raw) {}

    public record CelcoinBalanceResponse(String accountId, BigDecimal available, BigDecimal blocked, String currency, Map<String, Object> raw) {}

    public record CelcoinStatementRequest(@NotBlank String accountId, LocalDate startDate, LocalDate endDate) {}

    public record CelcoinStatementResponse(String accountId, java.util.List<CelcoinStatementTransactionResponse> transactions, Map<String, Object> raw) {}

    public record CelcoinStatementTransactionResponse(
            String transactionId, OffsetDateTime createdAt, BigDecimal amount, String type, String status, Map<String, Object> raw) {}

    public record CelcoinInternalTransferRequest(
            @NotBlank String sourceAccountId, @NotBlank String targetAccountId, BigDecimal amount, String description) {}

    public record CelcoinInternalTransferResponse(String transferId, String status, Map<String, Object> raw) {}
}
