package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.OffsetDateTime;
import com.brunopedraca.celcoin.webhook.WebhookDtos.*;

public interface CelcoinWebhookOperations {
    List<CelcoinWebhookEventResponse> listEvents();

    CelcoinWebhookEventResponse retry(UUID id);

    /** Removes local webhook audit records older than the supplied retention cutoff. */
    int purgeEventsBefore(OffsetDateTime cutoff);

    /** Normalizes a received antifraud/FtM payload without inventing a remote rule-management API. */
    CelcoinAntifraudEvent parseAntifraud(Map<String, Object> payload);

    WebhookSubscriptionResponse register(WebhookSubscriptionRequest request, String idempotencyKey);

    WebhookSubscriptionsResponse listSubscriptions(String entity, Boolean active);

    WebhookSubscriptionResponse update(String entity, WebhookSubscriptionUpdateRequest request, String idempotencyKey);

    WebhookSubscriptionResponse delete(String entity, String subscriptionId, String idempotencyKey);

    WebhookEntitiesResponse listEntities();

    WebhookTemplatesResponse listTemplates(WebhookTemplateQuery query);

    WebhookReplaySummary countReplays(WebhookReplayQuery query);

    WebhookReplayDetailsResponse replayDetails(WebhookReplayQuery query);

    WebhookReplaySummary resend(WebhookReplayQuery query, WebhookReplayFilter filter, String idempotencyKey);

    WebhookSubscriptionResponse registerBricks(
            CelBricksWebhookSubscriptionRequest request, String idempotencyKey);

    WebhookSubscriptionsResponse listBricks(String context, String entity, Boolean active);

    WebhookSubscriptionResponse updateBricks(
            String context, String entity, WebhookSubscriptionUpdateRequest request, String idempotencyKey);

    WebhookSubscriptionResponse deleteBricks(
            String context, String entity, String subscriptionId, String idempotencyKey);

    WebhookReplaySummary countBricksReplays(String context, WebhookReplayQuery query);

    WebhookReplayDetailsResponse bricksReplayDetails(String context, WebhookReplayQuery query);

    WebhookReplaySummary resendBricks(
            String context, WebhookReplayQuery query, WebhookReplayFilter filter, String idempotencyKey);

    record WebhookSubscriptionResponse(String subscriptionId, String status, String version, Map<String, Object> raw) {}
}
