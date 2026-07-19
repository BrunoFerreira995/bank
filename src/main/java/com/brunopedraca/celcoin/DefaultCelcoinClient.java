package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookOperations;

public record DefaultCelcoinClient(
        CelcoinTokenService authentication,
        CelcoinAccountOperations accounts,
        CelcoinPixOperations pix,
        CelcoinBoletoOperations boletos,
        CelcoinWebhookOperations webhooks)
        implements CelcoinClient {}
