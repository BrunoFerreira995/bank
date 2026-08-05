package com.brunopedraca.celcoin.webhook;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class WebhookDtos {
    private WebhookDtos() {}

    public record CelcoinWebhookReceipt(
            UUID id, String externalEventId, WebhookProcessingStatus status, boolean duplicate) {}

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
