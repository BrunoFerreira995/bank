package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.acquiring.CelcoinAcquiringOperations;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoOperations;
import com.brunopedraca.celcoin.cards.CelcoinCardOperations;
import com.brunopedraca.celcoin.onboarding.CelcoinOnboardingOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import com.brunopedraca.celcoin.pixauto.CelcoinPixAutoOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookOperations;

/** Public SDK facade. Domain interfaces are exposed to make consumer-side mocks simple. */
public interface CelcoinClient {
    CelcoinTokenService authentication();

    CelcoinAcquiringOperations acquiring();

    CelcoinAccountOperations accounts();

    CelcoinOnboardingOperations onboarding();

    CelcoinPixOperations pix();

    CelcoinPixAutoOperations pixAuto();

    CelcoinBoletoOperations boletos();

    CelcoinCardOperations cards();

    CelcoinWebhookOperations webhooks();
}
