package com.brunopedraca.celcoin.banking;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceBlockRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class BalanceDtosSerializationTest {
    @Test
    void serializesBlockRequestWithCorrelationAndTags() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new CelcoinBalanceBlockRequest(
                "30023646094074", new BigDecimal("10.00"), "req-1", "corr-1", "GARANTIA", "Reserva",
                List.of(new CelcoinBalanceTag("processo", "123"))));

        assertThat(json).contains("\"correlationBlockedId\":\"corr-1\"");
        assertThat(json).contains("\"tags\"");
    }
}
