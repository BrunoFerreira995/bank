package com.brunopedraca.celcoin.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class WebhookDtos {
    private WebhookDtos() {}

    public record CelcoinWebhookReceipt(
            UUID id, String externalEventId, WebhookProcessingStatus status, boolean duplicate) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedWebhookEvent(
            String entity, String status, OffsetDateTime createTimestamp, CelcoinTedWebhookBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedWebhookBody(
            String id,
            String clientCode,
            String originalId,
            String originalClientCode,
            BigDecimal amount,
            String reason,
            CelcoinTedWebhookParty debitParty,
            CelcoinTedWebhookParty creditParty) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinTedWebhookParty(
            String bank,
            String account,
            String branch,
            String taxId,
            String name,
            String accountType,
            String personType) {}

    public record CelcoinWebhookEventResponse(
            UUID id,
            String externalEventId,
            String eventType,
            OffsetDateTime receivedAt,
            OffsetDateTime processedAt,
            WebhookProcessingStatus status,
            int retryCount,
            String lastError) {
        static CelcoinWebhookEventResponse from(CelcoinWebhookEvent event) {
            return new CelcoinWebhookEventResponse(
                    event.getId(),
                    event.getExternalEventId(),
                    event.getEventType(),
                    event.getReceivedAt(),
                    event.getProcessedAt(),
                    event.getProcessingStatus(),
                    event.getRetryCount(),
                    event.getLastError());
        }
    }
}
