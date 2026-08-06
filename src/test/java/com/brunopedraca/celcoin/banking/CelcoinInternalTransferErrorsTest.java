package com.brunopedraca.celcoin.banking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CelcoinInternalTransferErrorsTest {
    @Test
    void exposesOfficialErrorsAndActions() {
        assertThat(CelcoinInternalTransferErrors.all()).extracting(CelcoinInternalTransferErrors.Error::code)
                .contains("CBE095", "CBE100", "CBE123", "CBE312", "CBE666");
        assertThat(CelcoinInternalTransferErrors.find("CBE100").retryable()).isTrue();
        assertThat(CelcoinInternalTransferErrors.find("CBE312").retryable()).isFalse();
    }
}
