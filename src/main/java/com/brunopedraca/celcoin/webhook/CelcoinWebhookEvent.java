package com.brunopedraca.celcoin.webhook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "celcoin_webhook_event")
public class CelcoinWebhookEvent {
    @Id
    private UUID id;

    @Column(name = "external_event_id", nullable = false, unique = true)
    private String externalEventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "headers_json", nullable = false)
    private String headersJson;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private WebhookProcessingStatus processingStatus;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error")
    private String lastError;

    protected CelcoinWebhookEvent() {}

    public CelcoinWebhookEvent(
            UUID id, String externalEventId, String eventType, String payloadJson, String headersJson) {
        this.id = id;
        this.externalEventId = externalEventId;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.headersJson = headersJson;
        this.receivedAt = OffsetDateTime.now();
        this.processingStatus = WebhookProcessingStatus.RECEIVED;
    }

    public UUID getId() {
        return id;
    }

    public String getExternalEventId() {
        return externalEventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public String getHeadersJson() {
        return headersJson;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public WebhookProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void markProcessing() {
        this.processingStatus = WebhookProcessingStatus.PROCESSING;
    }

    public void markProcessed() {
        this.processingStatus = WebhookProcessingStatus.PROCESSED;
        this.processedAt = OffsetDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.processingStatus = WebhookProcessingStatus.FAILED;
        this.retryCount++;
        this.lastError = error;
    }
}
