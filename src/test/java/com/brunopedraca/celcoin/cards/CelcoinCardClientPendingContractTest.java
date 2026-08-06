package com.brunopedraca.celcoin.cards;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardAccountRequest;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import org.junit.jupiter.api.Test;

class CelcoinCardClientPendingContractTest {
    @Test
    void shouldRequireHttpClient() {
        assertThatThrownBy(() -> new CelcoinCardClient(null).getCardAccount("card-account-1"))
                .isInstanceOf(CelcoinIntegrationException.class)
                .hasMessageContaining("Celcoin card HTTP client is required");
    }
}
