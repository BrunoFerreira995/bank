package com.brunopedraca.celcoin.indirectpix;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.CelcoinFundsRecoveryRequest;
import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.CelcoinFundsRecoveryContact;
import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.CelcoinTrackingGraphParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class IndirectPixDtosSerializationTest {
    @Test
    void serializesFundsRecoveryUsingOfficialFields() throws Exception {
        var request = new CelcoinFundsRecoveryRequest(
                "AUTOMATED", "E9999901012341234123412345678900", "SCAM", "Fraude",
                new CelcoinFundsRecoveryContact("med@example.com", "+5511999999999"),
                new CelcoinTrackingGraphParameters(new BigDecimal("200.00"), 5, "PT2H", 10));

        String json = new ObjectMapper().writeValueAsString(request);

        assertThat(json).contains("\"rootEndToEnd\":\"E9999901012341234123412345678900\"");
        assertThat(json).contains("\"trackingGraphParameters\"");
        assertThat(json).contains("\"minTransactionAmount\":200.00");
    }
}
