package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.common.exception.CelcoinNotFoundException;
import com.brunopedraca.celcoin.common.exception.CelcoinValidationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookReceipt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.brunopedraca.celcoin.webhook.WebhookDtos.*;

@Service
public class CelcoinWebhookService implements CelcoinWebhookOperations {
    private final CelcoinWebhookEventRepository repository;
    private final CelcoinWebhookSignatureVerifier verifier;
    private final CelcoinProperties properties;
    private final ObjectMapper objectMapper;
    private final CelcoinHttpClient httpClient;

    @Autowired
    public CelcoinWebhookService(
            CelcoinWebhookEventRepository repository,
            CelcoinWebhookSignatureVerifier verifier,
            CelcoinProperties properties,
            ObjectMapper objectMapper) {
        this(repository, verifier, properties, objectMapper, null);
    }

    public CelcoinWebhookService(
            CelcoinWebhookEventRepository repository,
            CelcoinWebhookSignatureVerifier verifier,
            CelcoinProperties properties,
            ObjectMapper objectMapper,
            CelcoinHttpClient httpClient) {
        this.repository = repository;
        this.verifier = verifier;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Transactional
    public CelcoinWebhookReceipt receive(byte[] payload, HttpHeaders headers) {
        if (payload.length > properties.webhook().maxPayloadBytes()) {
            throw new CelcoinValidationException("Celcoin webhook payload exceeds configured limit");
        }
        verifier.verify(payload, headers);
        JsonNode json = parse(payload);
        String extractedExternalId = firstText(json, "id", "eventId", "externalEventId");
        if (extractedExternalId == null && json.path("body").isObject()) {
            extractedExternalId = firstText(json.path("body"), "id", "eventId", "externalEventId");
        }
        final String externalId = extractedExternalId == null
                ? headers.getFirst("x-celcoin-event-id") : extractedExternalId;
        String eventType = firstText(json, "type", "eventType", "event", "entity");
        if (externalId == null || eventType == null) {
            throw new CelcoinValidationException("Celcoin webhook payload must include an event id and event type");
        }
        return repository
                .findByExternalEventId(externalId)
                .map(existing -> new CelcoinWebhookReceipt(
                        existing.getId(), existing.getExternalEventId(), existing.getProcessingStatus(), true))
                .orElseGet(() -> {
                    CelcoinWebhookEvent event = new CelcoinWebhookEvent(
                            UUID.randomUUID(),
                            externalId,
                            eventType,
                            new String(payload, StandardCharsets.UTF_8),
                            headersToJson(headers));
                    CelcoinWebhookEvent saved = repository.save(event);
                    processAsync(saved.getId());
                    return new CelcoinWebhookReceipt(
                            saved.getId(), saved.getExternalEventId(), saved.getProcessingStatus(), false);
                });
    }

    public List<CelcoinWebhookEventResponse> listEvents() {
        return repository.findAll().stream()
                .map(CelcoinWebhookEventResponse::from)
                .toList();
    }

    @Transactional
    public CelcoinWebhookEventResponse retry(UUID id) {
        CelcoinWebhookEvent event =
                repository.findById(id).orElseThrow(() -> new CelcoinNotFoundException("Webhook event not found"));
        process(event);
        return CelcoinWebhookEventResponse.from(event);
    }

    @Override
    public WebhookSubscriptionResponse register(WebhookSubscriptionRequest request, String idempotencyKey) {
        ensureRemote();
        require(request != null && hasText(request.entity()), "entity is required");
        require(request != null && hasText(request.webhookUrl()), "webhookUrl is required");
        Map<String, Object> raw = httpClient.post("/baas/v2/webhook/subscription", request, Map.class, context(idempotencyKey));
        return subscriptionResponse(raw);
    }

    @Override
    public WebhookSubscriptionsResponse listSubscriptions(String entity, Boolean active) {
        ensureRemote();
        Map<String, Object> raw = httpClient.get("/baas/v2/webhook/subscription?" + query("Entity", entity, "Active", active), Map.class, context(null));
        Map<String, Object> body = body(raw);
        List<WebhookSubscription> subscriptions = new ArrayList<>();
        Object values = body.get("subscriptions");
        if (values instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> map) subscriptions.add(subscription(cast(map)));
        return new WebhookSubscriptionsResponse(subscriptions, raw);
    }

    @Override
    public WebhookSubscriptionResponse update(String entity, WebhookSubscriptionUpdateRequest request, String idempotencyKey) {
        ensureRemote();
        require(hasText(entity), "entity is required");
        require(request != null, "webhook update is required");
        Map<String, Object> raw = httpClient.put("/baas/v2/webhook/subscription/" + encode(entity), request, Map.class, context(idempotencyKey));
        return subscriptionResponse(raw);
    }

    @Override
    public WebhookSubscriptionResponse delete(String entity, String subscriptionId, String idempotencyKey) {
        ensureRemote();
        require(hasText(entity), "entity is required");
        Map<String, Object> raw = httpClient.delete("/baas/v2/webhook/subscription/" + encode(entity) + "?" + query("SubscriptionId", subscriptionId), Map.of(), Map.class, context(idempotencyKey));
        return subscriptionResponse(raw);
    }

    @Override
    public WebhookEntitiesResponse listEntities() {
        ensureRemote();
        Map<String, Object> raw = httpClient.get("/baas/v2/webhook/entity/list", Map.class, context(null));
        Map<String, Object> body = body(raw);
        List<String> entities = new ArrayList<>();
        Object values = body.get("entityList");
        if (values instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> map && cast(map).get("entity") != null) entities.add(String.valueOf(cast(map).get("entity")));
        return new WebhookEntitiesResponse(entities, raw);
    }

    @Override
    public WebhookTemplatesResponse listTemplates(WebhookTemplateQuery query) {
        ensureRemote();
        WebhookTemplateQuery q = query == null ? new WebhookTemplateQuery(null, null, null, null, null) : query;
        Map<String, Object> raw = httpClient.get("/baas/v2/webhook/templates?" + query("Page", q.page(), "Limit", q.limit(), "LimitPerPage", q.limitPerPage(), "Entity", q.entity(), "Status", q.status()), Map.class, context(null));
        Map<String, Object> body = body(raw);
        List<Map<String, Object>> templates = listMaps(body.get("entityList"));
        if (templates.isEmpty()) templates = listMaps(body.get("webhookTemplates"));
        return new WebhookTemplatesResponse(templates, integer(body, "totalItems"), integer(body, "currentPage"), integer(body, "limitPerPage"), integer(body, "totalPages"), raw);
    }

    @Override
    public WebhookReplaySummary countReplays(WebhookReplayQuery query) {
        ensureRemote();
        Map<String, Object> raw = httpClient.get(replayPath(query, false), Map.class, context(null));
        return replaySummary(raw);
    }

    @Override
    public WebhookReplayDetailsResponse replayDetails(WebhookReplayQuery query) {
        ensureRemote();
        Map<String, Object> raw = httpClient.get(replayPath(query, true), Map.class, context(null));
        Map<String, Object> body = body(raw);
        return new WebhookReplayDetailsResponse(listMaps(body.get("webhookDetails")), integer(body, "totalItems"), integer(body, "currentPage"), integer(body, "limitPerPage"), integer(body, "totalPages"), raw);
    }

    @Override
    public WebhookReplaySummary resend(WebhookReplayQuery query, WebhookReplayFilter filter, String idempotencyKey) {
        ensureRemote();
        Map<String, Object> raw = httpClient.put(replayPath(query, false), filter == null ? Map.of() : filter, Map.class, context(idempotencyKey));
        return replaySummary(raw);
    }

    @Async
    @Transactional
    public void processAsync(UUID id) {
        repository.findById(id).ifPresent(this::process);
    }

    private void process(CelcoinWebhookEvent event) {
        try {
            event.markProcessing();
            event.markProcessed();
        } catch (Exception e) {
            event.markFailed(e.getMessage());
        }
    }

    private JsonNode parse(byte[] payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception e) {
            throw new CelcoinValidationException("Invalid Celcoin webhook JSON payload");
        }
    }

    private String firstText(JsonNode json, String... names) {
        for (String name : names) {
            JsonNode node = json.get(name);
            if (node != null && node.isTextual() && !node.asText().isBlank()) {
                return node.asText();
            }
        }
        return null;
    }

    private String headersToJson(HttpHeaders headers) {
        try {
            return objectMapper.writeValueAsString(Map.copyOf(headers));
        } catch (Exception e) {
            return "{}";
        }
    }

    private void ensureRemote() { if (httpClient == null) throw new IllegalStateException("Celcoin webhook client is not configured"); }
    private static void require(boolean condition, String message) { if (!condition) throw new CelcoinValidationException(message); }
    private static boolean hasText(String value) { return value != null && !value.isBlank(); }
    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static String replayPath(WebhookReplayQuery q, boolean details) {
        require(q != null && hasText(q.entity()), "entity is required");
        String suffix = details ? "/details" : "";
        return "/baas/v2/webhook/replay/" + encode(q.entity()) + suffix + "?" + query(
                "DateFrom", q.dateFrom(), "DateTo", q.dateTo(), "OnlyPending", q.onlyPending(), "webhookId", q.webhookId(),
                "documentNumber", q.documentNumber(), "account", q.account(), "id", q.id(), "clientRequestId", q.clientRequestId(),
                "Page", q.page(), "Limit", q.limit(), "LimitPerPage", q.limitPerPage());
    }
    private static WebhookReplaySummary replaySummary(Map<String, Object> raw) { Map<String, Object> body = body(raw); return new WebhookReplaySummary(text(body, "entity"), text(body, "dateFrom"), text(body, "dateTo"), bool(body, "onlyPending"), integer(body, "totalItems"), raw); }
    private static WebhookSubscriptionResponse subscriptionResponse(Map<String, Object> raw) { Map<String, Object> body = body(raw); return new WebhookSubscriptionResponse(text(body, "subscriptionId"), text(raw, "status"), text(raw, "version"), raw); }
    private static WebhookSubscription subscription(Map<String, Object> raw) { return new WebhookSubscription(text(raw, "subscriptionId"), text(raw, "entity"), text(raw, "webhookUrl"), bool(raw, "active"), text(raw, "createDate"), text(raw, "lastUpdateDate"), null, raw); }
    private static Map<String, Object> body(Map<String, Object> raw) { return raw != null && raw.get("body") instanceof Map<?, ?> map ? cast(map) : raw == null ? Map.of() : raw; }
    private static List<Map<String, Object>> listMaps(Object value) { List<Map<String, Object>> result = new ArrayList<>(); if (value instanceof List<?> list) for (Object item : list) if (item instanceof Map<?, ?> map) result.add(cast(map)); return result; }
    private static String query(Object... values) { StringBuilder result = new StringBuilder(); for (int i = 0; i < values.length; i += 2) if (values[i + 1] != null && (!values[i + 1].getClass().equals(String.class) || hasText(String.valueOf(values[i + 1])))) { if (!result.isEmpty()) result.append('&'); result.append(values[i]).append('=').append(encode(String.valueOf(values[i + 1]))); } return result.toString(); }
    private static String encode(String value) { return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8); }
    private static String text(Map<String, Object> map, String key) { return map == null || map.get(key) == null ? null : String.valueOf(map.get(key)); }
    private static Integer integer(Map<String, Object> map, String key) { try { return text(map, key) == null ? null : Integer.valueOf(text(map, key)); } catch (Exception ignored) { return null; } }
    private static Boolean bool(Map<String, Object> map, String key) { String value = text(map, key); return value == null ? null : Boolean.valueOf(value); }
    @SuppressWarnings("unchecked") private static Map<String, Object> cast(Map<?, ?> map) { return (Map<String, Object>) map; }
}
