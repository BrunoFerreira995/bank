package com.brunopedraca.celcoin.credit;

import java.util.Map;

/** Evento persistido de escrituração, repasse ou alteração de vínculo. */
public record CelcoinGuaranteeEvent(
        String id,
        String annotationId,
        String eventType,
        Map<String, Object> payload,
        String createdAt) {
}
