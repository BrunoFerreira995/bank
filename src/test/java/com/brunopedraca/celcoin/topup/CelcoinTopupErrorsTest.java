package com.brunopedraca.celcoin.topup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CelcoinTopupErrorsTest {
    @Test
    void exposesTopupErrorCatalog() {
        assertThat(CelcoinTopupErrors.all()).extracting(CelcoinTopupErrors.Error::code)
                .contains("ITBE003", "ITBE011", "ITBE045", "ITBE028");
        assertThat(CelcoinTopupErrors.find("ITBE011").retryable()).isFalse();
    }
}
