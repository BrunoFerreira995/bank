package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import java.util.List;
import java.util.UUID;

public interface CelcoinWebhookOperations {
    List<CelcoinWebhookEventResponse> listEvents();

    CelcoinWebhookEventResponse retry(UUID id);
}
