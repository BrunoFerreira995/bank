package com.brunopedraca.celcoin.bff.v1.identity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MobileIdentityCryptoTest {
    @Test
    void hashesOpaqueTokensDeterministicallyWithoutKeepingTheirPlaintext() {
        String token = TokenHasher.newToken();
        assertThat(token).hasSizeGreaterThan(30);
        assertThat(TokenHasher.hash(token)).hasSize(64).isEqualTo(TokenHasher.hash(token));
    }

    @Test
    void validatesStandardTotpAndRejectsAnInvalidCode() {
        // Base32("Hello!\\xDE\\xAD\\xBE\\xEF") and the RFC 6238 time step at 59 seconds.
        assertThat(TotpVerifier.matches("JBSWY3DPEHPK3PXP", "282760", 59_000)).isTrue();
        assertThat(TotpVerifier.matches("JBSWY3DPEHPK3PXP", "000000", 59_000)).isFalse();
    }
}
