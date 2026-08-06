package com.brunopedraca.celcoin.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CelcoinBaasExceptionTest {
    @Test
    void extractsNestedRemoteCodeFromApiEnvelope() {
        var exception = CelcoinBaasException.from(
                "{\"status\":\"ERROR\",\"error\":{\"errorCode\":\"CBE123\",\"message\":\"Saldo insuficiente\"}}",
                HttpStatus.BAD_REQUEST, "corr-1", "req-1");

        assertThat(exception).isInstanceOf(CelcoinBaasException.class);
        assertThat(exception.remoteCode()).isEqualTo("CBE123");
        assertThat(((CelcoinBaasException) exception).error().action()).contains("saldo");
    }
}
