package com.brunopedraca.celcoin.cards;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class CardDtos {
    private CardDtos() {}

    public record CelcoinCardAccountRequest(
            @NotBlank String document,
            @NotBlank String name,
            String email,
            String phone,
            CelcoinCardAddress address,
            Map<String, Object> metadata) {}

    public record CelcoinCardAccountResponse(
            String cardAccountId, String document, String name, String status, Map<String, Object> raw) {}

    public record CelcoinCardLimitResponse(
            String cardAccountId, BigDecimal totalLimit, BigDecimal availableLimit, BigDecimal usedLimit, String currency, Map<String, Object> raw) {}

    public record CelcoinCardCustomerUpdateRequest(
            @NotBlank String cardAccountId,
            String name,
            String email,
            String phone,
            CelcoinCardAddress address,
            Map<String, Object> metadata) {}

    public record CelcoinCardPhoneUpdateRequest(@NotBlank String cardAccountId, @NotBlank String phone) {}

    public record CelcoinCardAddress(
            String street, String number, String complement, String neighborhood, String city, String state, String postalCode, String country) {}

    public record CelcoinCardAddressRequest(@NotBlank String cardAccountId, CelcoinCardAddress address) {}

    public record CelcoinCardIssueRequest(
            @NotBlank String cardAccountId, String holderName, String type, CelcoinCardAddress deliveryAddress, Map<String, Object> metadata) {}

    public record CelcoinCardResponse(
            String cardId, String cardAccountId, String type, String status, String lastFourDigits, Map<String, Object> raw) {}

    public record CelcoinCardTrackingResponse(
            String cardId, String trackingCode, String carrier, String status, List<Map<String, Object>> events, Map<String, Object> raw) {}

    public record CelcoinCardStatusUpdateRequest(@NotBlank String cardId, @NotBlank String status, String reason) {}

    public record CelcoinCardSensitiveDataResponse(
            String cardId, String number, String holderName, String expirationMonth, String expirationYear, String securityCode, Map<String, Object> raw) {}

    public record CelcoinCardListRequest(String cardAccountId, String status, String type, Integer page, Integer size) {}

    public record CelcoinCardListResponse(List<CelcoinCardResponse> cards, Integer page, Integer size, Long total, Map<String, Object> raw) {}

    public record CelcoinCardPinUpdateRequest(@NotBlank String cardId, String onlinePin, String offlinePin) {}

    public record CelcoinCardTransactionSimulationRequest(
            @NotBlank String cardId, BigDecimal amount, String merchantName, String merchantCategoryCode, Map<String, Object> metadata) {}

    public record CelcoinCardTransactionSimulationResponse(
            String simulationId, String cardId, BigDecimal amount, String status, Map<String, Object> raw) {}

    public record CelcoinCardWebhookRequest(
            @NotBlank String eventType, @NotBlank String url, String secret, Map<String, Object> metadata) {}

    public record CelcoinCardWebhookResponse(String webhookId, String eventType, String url, String status, Map<String, Object> raw) {}

    public record CelcoinCardWebhookTemplateResponse(String eventType, Map<String, Object> payload, Map<String, Object> raw) {}

    public record CelcoinCardWebhookResendRequest(@NotBlank String webhookEventId) {}

    public record CelcoinCardInvoiceResponse(
            String invoiceId,
            String cardAccountId,
            BigDecimal amount,
            BigDecimal minimumAmount,
            String status,
            OffsetDateTime dueAt,
            Map<String, Object> raw) {}
}
