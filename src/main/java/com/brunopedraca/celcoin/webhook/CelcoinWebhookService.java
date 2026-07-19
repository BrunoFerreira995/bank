package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.common.exception.CelcoinNotFoundException;
import com.brunopedraca.celcoin.common.exception.CelcoinValidationException;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookReceipt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CelcoinWebhookService implements CelcoinWebhookOperations {
    private final CelcoinWebhookEventRepository repository;
    private final CelcoinWebhookSignatureVerifier verifier;
    private final CelcoinProperties properties;
    private final ObjectMapper objectMapper;

    public CelcoinWebhookService(
            CelcoinWebhookEventRepository repository,
            CelcoinWebhookSignatureVerifier verifier,
            CelcoinProperties properties,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.verifier = verifier;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CelcoinWebhookReceipt receive(byte[] payload, HttpHeaders headers) {
        if (payload.length > properties.webhook().maxPayloadBytes()) {
            throw new CelcoinValidationException("Celcoin webhook payload exceeds configured limit");
        }
        verifier.verify(payload, headers);
        JsonNode json = parse(payload);
        String externalId = firstText(json, "id", "eventId", "externalEventId");
        String eventType = firstText(json, "type", "eventType");
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
                    return new CelcoinWebhookReceipt(saved.getId(), saved.getExternalEventId(), saved.getProcessingStatus(), false);
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
}
