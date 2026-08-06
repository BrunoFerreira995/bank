package com.brunopedraca.celcoin.credit;

import java.time.OffsetDateTime;
import java.util.Map;

/** Devolutiva assíncrona da avaliação da oferta no leilão interno. */
public record CelcoinWorkersCreditAuctionEvent(
        String event,
        Map<String, Object> payload) {

    public String proposalId() {
        return payload == null || payload.get("id") == null ? null : String.valueOf(payload.get("id"));
    }

    public String status() {
        return payload == null || payload.get("status") == null ? null : String.valueOf(payload.get("status"));
    }

    public OffsetDateTime timestamp() {
        Object value = payload == null ? null : payload.get("timestamp");
        return value == null ? null : OffsetDateTime.parse(String.valueOf(value));
    }

    public boolean approved() { return "APPROVED".equals(status()); }
    public boolean denied() { return "DENIED".equals(status()); }
    public boolean error() { return "ERROR".equals(status()); }
}
