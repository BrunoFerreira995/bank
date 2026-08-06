package com.brunopedraca.celcoin.slc;

import com.brunopedraca.celcoin.slc.SlcDtos.PaymentInEvent;
import com.brunopedraca.celcoin.slc.SlcDtos.SettlementErrorEvent;
import java.util.List;

/** Operações de recebimento e interpretação dos eventos SLC enviados por webhook. */
public interface CelcoinSlcOperations {
    String PAYMENT_IN_ENTITY = "slc-payment-in";
    String ERROR_ENTITY = "spb-event-error";

    PaymentInEvent parsePaymentIn(String payload);

    SettlementErrorEvent parseSettlementError(String payload);

    default boolean supports(String entity) {
        return PAYMENT_IN_ENTITY.equals(entity) || ERROR_ENTITY.equals(entity);
    }

    default List<String> supportedEntities() {
        return List.of(PAYMENT_IN_ENTITY, ERROR_ENTITY);
    }
}
