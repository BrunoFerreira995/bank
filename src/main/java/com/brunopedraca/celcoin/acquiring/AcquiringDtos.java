package com.brunopedraca.celcoin.acquiring;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class AcquiringDtos {
    private AcquiringDtos() {}

    public record CelcoinAcquiringAccreditationStatusResponse(
            String accountId, String status, String reason, OffsetDateTime updatedAt, Map<String, Object> raw) {}

    public record CelcoinAcquiringCustomerRequest(
            String customerId,
            @NotBlank String document,
            @NotBlank String name,
            String email,
            String phone,
            Map<String, Object> address,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringCustomerResponse(
            String customerId, String document, String name, String status, Map<String, Object> raw) {}

    public record CelcoinAcquiringListRequest(
            String accountId, String customerId, String status, Integer page, Integer size) {}

    public record CelcoinAcquiringCustomerListResponse(
            List<CelcoinAcquiringCustomerResponse> customers,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringCardRequest(
            @NotBlank String customerId,
            @NotBlank String holderName,
            @NotBlank String number,
            @NotBlank String expirationMonth,
            @NotBlank String expirationYear,
            String securityCode,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringCardResponse(
            String cardId,
            String customerId,
            String brand,
            String lastFourDigits,
            String status,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringCardListResponse(
            List<CelcoinAcquiringCardResponse> cards,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringChargeRequest(
            String chargeId,
            @NotBlank String customerId,
            String cardId,
            BigDecimal amount,
            String currency,
            String description,
            Boolean capture,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringChargeResponse(
            String chargeId,
            String customerId,
            String transactionId,
            BigDecimal amount,
            String status,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringChargeListResponse(
            List<CelcoinAcquiringChargeResponse> charges,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringReceivablesReportRequest(
            @NotBlank String accountId, LocalDate startDate, LocalDate endDate, Map<String, Object> filters) {}

    public record CelcoinAcquiringReceivablesReportResponse(
            String reportId, String accountId, String status, String downloadUrl, Map<String, Object> raw) {}

    public record CelcoinAcquiringPlanRequest(
            String planId,
            @NotBlank String name,
            BigDecimal amount,
            String currency,
            String interval,
            Integer intervalCount,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringPlanResponse(
            String planId, String name, BigDecimal amount, String status, Map<String, Object> raw) {}

    public record CelcoinAcquiringPlanListResponse(
            List<CelcoinAcquiringPlanResponse> plans,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringSubscriptionRequest(
            String subscriptionId,
            @NotBlank String customerId,
            String planId,
            String cardId,
            BigDecimal amount,
            String interval,
            LocalDate startDate,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringSubscriptionResponse(
            String subscriptionId, String customerId, String planId, String status, Map<String, Object> raw) {}

    public record CelcoinAcquiringSubscriptionListResponse(
            List<CelcoinAcquiringSubscriptionResponse> subscriptions,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringSubscriptionPaymentUpdateRequest(
            @NotBlank String subscriptionId, String cardId, Map<String, Object> paymentMethod) {}

    public record CelcoinAcquiringSubscriptionTransactionRequest(
            String transactionId,
            @NotBlank String subscriptionId,
            BigDecimal amount,
            LocalDate dueDate,
            Map<String, Object> metadata) {}

    public record CelcoinAcquiringChargebackListResponse(
            List<CelcoinAcquiringChargebackResponse> chargebacks,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinAcquiringChargebackResponse(
            String chargebackId, String transactionId, BigDecimal amount, String status, Map<String, Object> raw) {}

    public record CelcoinAcquiringChargebackDefenseRequest(
            @NotBlank String chargebackId, List<Map<String, Object>> documents, String notes) {}

    public record CelcoinAcquiringWebhookRequest(
            @NotBlank String eventType, @NotBlank String url, String secret, Map<String, Object> metadata) {}

    public record CelcoinAcquiringWebhookResponse(
            String webhookId, String eventType, String url, String status, Map<String, Object> raw) {}

    public record CelcoinAcquiringCardTokenRequest(
            @NotBlank String holderName,
            @NotBlank String number,
            @NotBlank String expirationMonth,
            @NotBlank String expirationYear,
            String securityCode) {}

    public record CelcoinAcquiringCardTokenResponse(
            String tokenId, String brand, String lastFourDigits, Map<String, Object> raw) {}

    public record CelcoinAcquiringFeeListResponse(List<Map<String, Object>> fees, Map<String, Object> raw) {}

    public record CelcoinAcquiringTransactionListResponse(
            List<Map<String, Object>> transactions, Integer page, Integer size, Long total, Map<String, Object> raw) {}

    public record CelcoinAcquiringReceivablesStatementResponse(
            String accountId,
            List<Map<String, Object>> entries,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}
}
