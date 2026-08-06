package com.brunopedraca.celcoin.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CelcoinBaasErrorsTest {
    @Test
    void mapsOfficialCodeByModuleAndRetryability() {
        var error = CelcoinBaasErrors.find("CBE100");

        assertThat(error.modules()).contains("pix", "internal-transfer");
        assertThat(error.retryable()).isTrue();
        assertThat(error.action()).contains("status");
    }

    @Test
    void returnsSafeDescriptorForUnknownCode() {
        var error = CelcoinBaasErrors.find("CBE9999");

        assertThat(error.modules()).containsExactly("unknown");
        assertThat(error.message()).contains("não mapeado");
    }
}
