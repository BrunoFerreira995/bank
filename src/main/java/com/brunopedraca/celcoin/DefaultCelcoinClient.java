package com.brunopedraca.celcoin;

import com.brunopedraca.celcoin.acquiring.CelcoinAcquiringOperations;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoOperations;
import com.brunopedraca.celcoin.cards.CelcoinCardOperations;
import com.brunopedraca.celcoin.credit.CelcoinCreditOperations;
import com.brunopedraca.celcoin.onboarding.CelcoinOnboardingOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import com.brunopedraca.celcoin.pixauto.CelcoinPixAutoOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookOperations;
import com.brunopedraca.celcoin.vehicle.CelcoinVehicleOperations;

public record DefaultCelcoinClient(
        CelcoinTokenService authentication,
        CelcoinAcquiringOperations acquiring,
        CelcoinAccountOperations accounts,
        CelcoinOnboardingOperations onboarding,
        CelcoinPixOperations pix,
        CelcoinPixAutoOperations pixAuto,
        CelcoinBoletoOperations boletos,
        CelcoinCardOperations cards,
        CelcoinWebhookOperations webhooks,
        CelcoinCreditOperations credit,
        CelcoinVehicleOperations vehicles)
        implements CelcoinClient {}
