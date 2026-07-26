package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoOperations;
import com.brunopedraca.celcoin.onboarding.CelcoinOnboardingOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookOperations;

public record DefaultCelcoinClient(
        CelcoinTokenService authentication,
        CelcoinAccountOperations accounts,
        CelcoinOnboardingOperations onboarding,
        CelcoinPixOperations pix,
        CelcoinBoletoOperations boletos,
        CelcoinWebhookOperations webhooks)
        implements CelcoinClient {}
