package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.brunopedraca.celcoin.webhook.WebhookDtos.*;

public interface CelcoinWebhookOperations {
    List<CelcoinWebhookEventResponse> listEvents();

    CelcoinWebhookEventResponse retry(UUID id);

    WebhookSubscriptionResponse register(WebhookSubscriptionRequest request, String idempotencyKey);

    WebhookSubscriptionsResponse listSubscriptions(String entity, Boolean active);

    WebhookSubscriptionResponse update(String entity, WebhookSubscriptionUpdateRequest request, String idempotencyKey);

    WebhookSubscriptionResponse delete(String entity, String subscriptionId, String idempotencyKey);

    WebhookEntitiesResponse listEntities();

    WebhookTemplatesResponse listTemplates(WebhookTemplateQuery query);

    WebhookReplaySummary countReplays(WebhookReplayQuery query);

    WebhookReplayDetailsResponse replayDetails(WebhookReplayQuery query);

    WebhookReplaySummary resend(WebhookReplayQuery query, WebhookReplayFilter filter, String idempotencyKey);

    record WebhookSubscriptionResponse(String subscriptionId, String status, String version, Map<String, Object> raw) {}
}
