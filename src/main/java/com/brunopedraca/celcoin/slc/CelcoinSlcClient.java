package com.brunopedraca.celcoin.slc;

import com.brunopedraca.celcoin.common.exception.CelcoinValidationException;
import com.brunopedraca.celcoin.slc.SlcDtos.PaymentInEvent;
import com.brunopedraca.celcoin.slc.SlcDtos.SettlementErrorEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Parser dos webhooks SLC; não há endpoint de iniciação ou consulta no contrato público. */
public final class CelcoinSlcClient implements CelcoinSlcOperations {
    private final ObjectMapper objectMapper;

    public CelcoinSlcClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentInEvent parsePaymentIn(String payload) {
        return read(payload, PaymentInEvent.class);
    }

    @Override
    public SettlementErrorEvent parseSettlementError(String payload) {
        return read(payload, SettlementErrorEvent.class);
    }

    private <T> T read(String payload, Class<T> type) {
        if (payload == null || payload.isBlank()) {
            throw new CelcoinValidationException("SLC webhook payload is required");
        }
        try {
            return objectMapper.readValue(payload, type);
        } catch (Exception exception) {
            throw new CelcoinValidationException("Invalid SLC webhook JSON payload");
        }
    }
}
