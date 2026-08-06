package com.brunopedraca.celcoin.boleto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CelcoinBoletoPaymentErrorsTest {
    @Test
    void exposesOfficialCodesAndRetryGuidance() {
        assertThat(CelcoinBoletoPaymentErrors.all()).extracting(CelcoinBoletoPaymentErrors.Error::code)
                .contains("PCE009", "PCE025", "PCE026", "PCE050", "PCE092");
        assertThat(CelcoinBoletoPaymentErrors.find("PCE092").retryable()).isTrue();
        assertThat(CelcoinBoletoPaymentErrors.find("PCE025").retryable()).isFalse();
    }
}
