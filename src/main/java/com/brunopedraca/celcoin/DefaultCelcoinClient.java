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
import com.brunopedraca.celcoin.sweeping.CelcoinSweepingOperations;
import com.brunopedraca.celcoin.indirectpix.CelcoinIndirectPixOperations;
import com.brunopedraca.celcoin.cnab.CelcoinCnabOperations;
import com.brunopedraca.celcoin.openfinance.CelcoinOpenFinanceOperations;
import com.brunopedraca.celcoin.jsr.CelcoinJsrOperations;
import com.brunopedraca.celcoin.itp.CelcoinItpOperations;

public record DefaultCelcoinClient(
        CelcoinTokenService authentication,
        CelcoinAcquiringOperations acquiring,
        CelcoinAccountOperations accounts,
        CelcoinOnboardingOperations onboarding,
        CelcoinPixOperations pix,
        CelcoinPixAutoOperations pixAuto,
        CelcoinSweepingOperations sweeping,
        CelcoinIndirectPixOperations indirectPix,
        CelcoinCnabOperations cnab,
        CelcoinOpenFinanceOperations openFinance,
        CelcoinJsrOperations jsr,
        CelcoinItpOperations itp,
        CelcoinBoletoOperations boletos,
        CelcoinCardOperations cards,
        CelcoinWebhookOperations webhooks,
        CelcoinCreditOperations credit,
        CelcoinVehicleOperations vehicles)
        implements CelcoinClient {}
