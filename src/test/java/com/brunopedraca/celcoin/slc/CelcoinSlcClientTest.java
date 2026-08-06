package com.brunopedraca.celcoin.slc;

import static org.assertj.core.api.Assertions.assertThat;

import com.brunopedraca.celcoin.slc.SlcDtos.PaymentInEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CelcoinSlcClientTest {
    private final CelcoinSlcClient client = new CelcoinSlcClient(new ObjectMapper().findAndRegisterModules());

    @Test
    void parsesPaymentInWebhook() {
        PaymentInEvent event = client.parsePaymentIn("""
                {"entity":"slc-payment-in","status":"CONFIRMED","body":{
                  "id":"slc-1","amount":10.8,"balanceType":"CREDIT",
                  "movementAccount":{"account":"30053913793009","taxId":"14503144227"},
                  "tags":[{"key":"PaymentArrangement","value":"Visa"}]}}
                """);

        assertThat(event.entity()).isEqualTo("slc-payment-in");
        assertThat(event.body().amount()).isEqualByComparingTo(new BigDecimal("10.8"));
        assertThat(event.body().movementAccount().account()).isEqualTo("30053913793009");
        assertThat(event.body().tags()).singleElement().extracting(SlcDtos.Tag::value).isEqualTo("Visa");
    }

    @Test
    void parsesSettlementErrorWebhook() {
        var event = client.parseSettlementError("""
                {"entity":"spb-event-error","RequestBody":{"message":"SLC0001 saldo insuficiente"}}
                """);

        assertThat(event.entity()).isEqualTo("spb-event-error");
        assertThat(event.requestBody().message()).contains("SLC0001");
    }
}
