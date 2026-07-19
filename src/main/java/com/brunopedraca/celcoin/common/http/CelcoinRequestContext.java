package com.brunopedraca.celcoin.common.http;

import java.util.UUID;

public record CelcoinRequestContext(String correlationId, String idempotencyKey) {
    public static CelcoinRequestContext create(String idempotencyKey) {
        return new CelcoinRequestContext(UUID.randomUUID().toString(), idempotencyKey);
    }
}
