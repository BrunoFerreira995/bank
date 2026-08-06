package com.brunopedraca.celcoin.banking;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public final class AccountDtos {
    private AccountDtos() {}

    public record CelcoinAccountStatusResponse(String accountId, String status, Map<String, Object> raw) {}

    public record CelcoinAccountResponse(
            String accountId, String document, String type, String status, Map<String, Object> raw) {}

    public record CelcoinCoreAccountRequest(
            @NotBlank String document,
            @NotBlank String name,
            String type,
            String email,
            String phone,
            CelcoinAccountAddress address,
            Map<String, Object> metadata) {}

    public record CelcoinAccountAddress(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String postalCode,
            String country) {}

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

    public record CelcoinAccountClosureRequest(
            @NotBlank String accountId, String reason, Map<String, Object> metadata) {}

    public record CelcoinAccountListRequest(String document, String type, String status, Integer page, Integer size) {}

    public record CelcoinAccountListResponse(
            List<CelcoinAccountResponse> accounts, Integer page, Integer size, Long total, Map<String, Object> raw) {}

    public record CelcoinAccountCountResponse(Long total, Map<String, Object> raw) {}

    public record CelcoinJudicialBlockRequest(
            @NotBlank String accountId, BigDecimal amount, String processNumber, String reason) {}

    public record CelcoinJudicialBlockResponse(
            String blockId, String accountId, BigDecimal amount, String status, Map<String, Object> raw) {}

    public record CelcoinBalanceTag(String key, String value) {}

    public record CelcoinBalanceBlockRequest(
            @NotBlank String accountId,
            BigDecimal amount,
            @NotBlank String clientRequestId,
            @NotBlank String correlationBlockedId,
            @NotBlank String reason,
            @NotBlank String description,
            List<CelcoinBalanceTag> tags) {}

    public record CelcoinBalanceUnblockRequest(
            @NotBlank String accountId,
            @NotBlank String clientRequestId,
            @NotBlank String correlationBlockedId,
            @NotBlank String reason,
            @NotBlank String description,
            Boolean unBlockAll,
            BigDecimal amount) {}

    public record CelcoinBalanceOperationResponse(
            String status,
            String version,
            String id,
            BigDecimal amount,
            String clientRequestId,
            String correlationBlockedId,
            Map<String, Object> raw) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinBalanceAmountWebhookEvent(
            String webhookId, String entity, String createTimestamp, String status, Map<String, Object> body) {}

    public record CelcoinAccountStatusUpdateRequest(
            @NotBlank String accountId, @NotBlank String status, String reason) {}

    public record CelcoinSandboxBalanceRequest(@NotBlank String accountId, BigDecimal amount, String description) {}

    public record CelcoinAccountMonitoringRequest(
            @NotBlank String accountId, String type, Map<String, Object> metadata) {}

    public record CelcoinAccountMonitoringResponse(
            String monitoringId, String accountId, String status, Map<String, Object> raw) {}

    public record CelcoinAccountMonitoringWebhookEvent(
            String eventId,
            String eventType,
            String monitoringId,
            String accountId,
            String status,
            OffsetDateTime occurredAt,
            Map<String, Object> raw) {}

    public record CelcoinBalanceResponse(
            String accountId, BigDecimal available, BigDecimal blocked, String currency, Map<String, Object> raw) {}

    public record CelcoinStatementRequest(
            @NotBlank String accountId,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        public CelcoinStatementRequest(String accountId, LocalDate startDate, LocalDate endDate) {
            this(accountId, startDate, endDate, null, null);
        }
    }

    public record CelcoinStatementResponse(
            String accountId,
            java.util.List<CelcoinStatementTransactionResponse> transactions,
            Integer page,
            Integer size,
            Long total,
            Boolean hasNext,
            Map<String, Object> raw) {
        public CelcoinStatementResponse(
                String accountId,
                java.util.List<CelcoinStatementTransactionResponse> transactions,
                Map<String, Object> raw) {
            this(accountId, transactions, null, null, null, null, raw);
        }
    }

    public record CelcoinStatementTransactionResponse(
            String transactionId,
            OffsetDateTime createdAt,
            BigDecimal amount,
            String type,
            String status,
            Map<String, Object> raw) {}

    public record CelcoinIncomeReportRequest(
            @NotBlank String accountId, Integer calendarYear, Integer quarter) {
        public CelcoinIncomeReportRequest(String accountId, Integer calendarYear) {
            this(accountId, calendarYear, null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinIncomeReportResponse(
            String version, String status, CelcoinIncomeReportBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinIncomeReportBody(
            CelcoinIncomeReportPayerSource payerSource,
            CelcoinIncomeReportOwner owner,
            CelcoinIncomeReportAccount account,
            List<CelcoinIncomeReportBalance> balances,
            String incomeFile,
            String fileType) {}

    public record CelcoinIncomeReportPayerSource(String name, String documentNumber) {}

    public record CelcoinIncomeReportOwner(String documentNumber, String name, String type, String createDate) {}

    public record CelcoinIncomeReportAccount(String branch, String account) {}

    public record CelcoinIncomeReportBalance(
            String calendarYear, BigDecimal amount, String currency, String type) {}

    public record CelcoinTedTransferRequest(
            @NotBlank String debitAccountId,
            BigDecimal amount,
            @NotBlank String clientFinality,
            CelcoinTedCreditParty creditParty,
            String clientCode,
            String description) {}

    public record CelcoinTedCreditParty(
            @NotBlank String bank,
            @NotBlank String account,
            @NotBlank String branch,
            @NotBlank String taxId,
            @NotBlank String name,
            @NotBlank String accountType,
            @NotBlank String personType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedTransferResponse(
            String version, String status, CelcoinTedTransferBody body, Map<String, Object> error) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedTransferBody(
            String id,
            BigDecimal amount,
            String clientCode,
            CelcoinTedParty debitParty,
            CelcoinTedParty creditParty,
            String clientFinality,
            String description) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedParty(
            String bank,
            String account,
            String branch,
            String taxId,
            String name,
            String accountType,
            String personType) {}

    public record CelcoinInternalTransferRequest(
            @NotBlank String sourceAccountId,
            @NotBlank String targetAccountId,
            BigDecimal amount,
            String description,
            String clientRequestId,
            String targetTaxId) {
        public CelcoinInternalTransferRequest(
                String sourceAccountId, String targetAccountId, BigDecimal amount, String description) {
            this(sourceAccountId, targetAccountId, amount, description, null, null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinInternalTransferResponse(
            String transferId,
            BigDecimal amount,
            String clientRequestId,
            String endToEndId,
            String status,
            Map<String, Object> raw) {
        public CelcoinInternalTransferResponse(String transferId, String status, Map<String, Object> raw) {
            this(transferId, null, null, null, status, raw);
        }
    }
}
