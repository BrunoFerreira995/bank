package com.brunopedraca.celcoin.credit;

import java.math.BigDecimal;

/** Dados aceitos no envio de uma oferta para o leilão interno. */
public record CelcoinWorkersCreditOffer(
        Integer installmentQuantity,
        BigDecimal installmentAmount,
        BigDecimal availableBalance,
        BigDecimal amount,
        BigDecimal iof,
        BigDecimal annualTax,
        BigDecimal cet,
        BigDecimal interestTax,
        BigDecimal monthlyCet,
        BigDecimal insuranceAmount,
        String entryUrl) {
}
