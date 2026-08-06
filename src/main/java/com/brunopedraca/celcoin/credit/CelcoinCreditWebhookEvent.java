package com.brunopedraca.celcoin.credit;

import java.time.OffsetDateTime;
import java.util.Map;

/** Evento assíncrono enviado ao originador pela Plataforma de Crédito. */
public record CelcoinCreditWebhookEvent(
        Map<String, Object> payload,
        OffsetDateTime createdAt,
        String type) {

    public boolean applicationStatusUpdated() {
        return "APPLICATION_STATUS_UPDATED".equals(type);
    }

    public boolean documentStatusUpdated() {
        return "PERSON_DOCUMENT_STATUS_UPDATED".equals(type)
                || "BUSINESS_DOCUMENT_STATUS_UPDATED".equals(type);
    }
}
