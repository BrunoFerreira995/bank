package com.brunopedraca.celcoin.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SensitiveDataMaskerTest {
    @Test
    void masksDocumentsAndSecrets() {
        String masked = SensitiveDataMasker.mask(
                "cpf 12345678910 cnpj 12345678000190 access_token: abc client_secret=def");

        assertThat(masked)
                .contains("123.***.***-10")
                .contains("12.***.***/****-90")
                .doesNotContain("abc")
                .doesNotContain("def");
    }
}
