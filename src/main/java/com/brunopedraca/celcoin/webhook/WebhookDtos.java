package com.brunopedraca.celcoin.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Map;
import java.util.List;

public final class WebhookDtos {
    private WebhookDtos() {}

    public record WebhookAuth(String login, String pwd, String type) {}

    public record WebhookSubscriptionRequest(
            String entity, String webhookUrl, WebhookAuth auth) {}

    /** Request used by the CEL_BRICKS common webhook manager. */
    public record CelBricksWebhookSubscriptionRequest(
            String context, String entity, String webhookUrl, WebhookAuth auth) {}

    public record WebhookSubscriptionUpdateRequest(
            String webhookUrl, WebhookAuth auth, Boolean active, String subscriptionId) {}

    public record WebhookSubscription(
            String subscriptionId,
            String entity,
            String webhookUrl,
            Boolean active,
            String createDate,
            String lastUpdateDate,
            WebhookAuth auth,
            Map<String, Object> raw) {}

    public record WebhookSubscriptionsResponse(
            List<WebhookSubscription> subscriptions, Map<String, Object> raw) {}

    public record WebhookEntitiesResponse(List<String> entities, Map<String, Object> raw) {}

    public record WebhookTemplateQuery(
            Integer page, Integer limit, Integer limitPerPage, String entity, String status) {}

    public record WebhookTemplatesResponse(
            List<Map<String, Object>> templates, Integer totalItems, Integer currentPage,
            Integer limitPerPage, Integer totalPages, Map<String, Object> raw) {}

    public record WebhookReplayQuery(
            String entity, String dateFrom, String dateTo, Boolean onlyPending,
            String webhookId, String documentNumber, String account, String id, String clientRequestId,
            Integer page, Integer limit, Integer limitPerPage) {}

    public record WebhookReplayFilter(
            String documentNumber, String account, String id, String clientRequestId) {}

    public record WebhookReplayDetailsResponse(
            List<Map<String, Object>> webhooks, Integer totalItems, Integer currentPage,
            Integer limitPerPage, Integer totalPages, Map<String, Object> raw) {}

    public record WebhookReplaySummary(
            String entity, String dateFrom, String dateTo, Boolean onlyPending,
            Integer totalItems, Map<String, Object> raw) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinInfractionBalanceEvent(
            String webhookId, String entity, String status, String createTimestamp,
            CelcoinInfractionBalanceBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinInfractionBalanceBody(
            String blockId,
            String fundsRecoveryId,
            String blockMode,
            String reason,
            String reasonDescription,
            String unblockReason,
            String unblockReasonDescription,
            String infractionId,
            CelcoinInfractionAmounts amounts,
            CelcoinBalanceAccount account,
            CelcoinBalanceTransaction transaction,
            String type) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinInfractionAmounts(
            BigDecimal requestedBlockAmount,
            BigDecimal blockedAmount,
            BigDecimal previusBlockedAmount,
            BigDecimal remainingToBlockAmount,
            BigDecimal totalBlockedAmount,
            BigDecimal unblockedAmount) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinBalanceAccount(
            String bank, String taxId, String name, String branch, String account) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinBalanceTransaction(
            CelcoinBalanceAccount debitParty, String endToEnd, CelcoinBalanceAccount creditParty) {}

    public record CelcoinWebhookReceipt(
            UUID id, String externalEventId, WebhookProcessingStatus status, boolean duplicate) {}

    /** Normalized view of an antifraud/FtM notification received by the local webhook endpoint. */
    public record CelcoinAntifraudEvent(
            String entity,
            String status,
            String transactionId,
            String endToEndId,
            String reason,
            BigDecimal amount,
            AntifraudDecision decision,
            Map<String, Object> raw) {}

    public enum AntifraudDecision {
        ALLOWED, BLOCKED, PENDING, RELEASED, REJECTED, UNKNOWN
    }

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinSlcWebhookEvent(
            String entity, String status, OffsetDateTime createTimestamp, CelcoinSlcWebhookBody body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinSlcWebhookBody(
            String id,
            String approvalId,
            String clientRequestId,
            String movementType,
            String balanceType,
            BigDecimal amount,
            CelcoinSlcMovementAccount movementAccount,
            java.util.List<CelcoinSlcTag> tags) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinSlcMovementAccount(String account, String taxId, String name, String branch) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinSlcTag(String key, String value) {}

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
