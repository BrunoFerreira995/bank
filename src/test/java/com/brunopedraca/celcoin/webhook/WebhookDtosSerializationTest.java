package com.brunopedraca.celcoin.webhook;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinInfractionBalanceEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class WebhookDtosSerializationTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void deserializesInfractionBlockEvent() throws Exception {
        CelcoinInfractionBalanceEvent event = mapper.readValue("""
                {"webhookId":"block-1","entity":"pix-med-balance-blocked","status":"PARTIAL_BLOCKED",
                 "body":{"blockId":"b-1","infractionId":"i-1","amounts":{"blockedAmount":20,"totalBlockedAmount":20},
                 "account":{"bank":"123","account":"456"}}}
                """, CelcoinInfractionBalanceEvent.class);

        assertThat(event.entity()).isEqualTo("pix-med-balance-blocked");
        assertThat(event.body().amounts().blockedAmount()).isEqualByComparingTo(new BigDecimal("20"));
        assertThat(event.body().account().account()).isEqualTo("456");
    }

    @Test
    void serializesSubscriptionRequest() throws Exception {
        String json = mapper.writeValueAsString(new WebhookDtos.WebhookSubscriptionRequest(
                "pix-payment-in", "https://example.com/webhook",
                new WebhookDtos.WebhookAuth("login", "pwd", "basic")));

        assertThat(json).contains("\"entity\":\"pix-payment-in\"");
        assertThat(json).contains("\"webhookUrl\":\"https://example.com/webhook\"");
    }
}
