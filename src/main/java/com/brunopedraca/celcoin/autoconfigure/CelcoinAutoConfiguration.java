package com.brunopedraca.celcoin.autoconfigure;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.DefaultCelcoinClient;
import com.brunopedraca.celcoin.acquiring.CelcoinAcquiringClient;
import com.brunopedraca.celcoin.acquiring.CelcoinAcquiringOperations;
import com.brunopedraca.celcoin.auth.CelcoinTokenClient;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.banking.CelcoinAccountClient;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoClient;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoOperations;
import com.brunopedraca.celcoin.cards.CelcoinCardClient;
import com.brunopedraca.celcoin.cards.CelcoinCardOperations;
import com.brunopedraca.celcoin.credit.CelcoinCreditClient;
import com.brunopedraca.celcoin.credit.CelcoinCreditOperations;
import com.brunopedraca.celcoin.credit.CelcoinCreditProperties;
import com.brunopedraca.celcoin.escrow.CelcoinEscrowClient;
import com.brunopedraca.celcoin.escrow.CelcoinEscrowOperations;
import com.brunopedraca.celcoin.escrow.CelcoinEscrowProperties;
import com.brunopedraca.celcoin.embedded.CelcoinEmbeddedClient;
import com.brunopedraca.celcoin.embedded.CelcoinEmbeddedOperations;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinSslContextProvider;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.brunopedraca.celcoin.common.http.NettyCelcoinSslContextProvider;
import com.brunopedraca.celcoin.common.idempotency.CelcoinIdempotencyRecordRepository;
import com.brunopedraca.celcoin.common.idempotency.CelcoinIdempotencyService;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import com.brunopedraca.celcoin.onboarding.CelcoinOnboardingClient;
import com.brunopedraca.celcoin.onboarding.CelcoinOnboardingOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixClient;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import com.brunopedraca.celcoin.pixauto.CelcoinPixAutoClient;
import com.brunopedraca.celcoin.pixauto.CelcoinPixAutoOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookOperations;
import com.brunopedraca.celcoin.webhook.CelcoinWebhookService;
import com.brunopedraca.celcoin.vehicle.CelcoinVehicleClient;
import com.brunopedraca.celcoin.vehicle.CelcoinVehicleOperations;
import com.brunopedraca.celcoin.sweeping.CelcoinSweepingClient;
import com.brunopedraca.celcoin.sweeping.CelcoinSweepingOperations;
import com.brunopedraca.celcoin.indirectpix.CelcoinIndirectPixClient;
import com.brunopedraca.celcoin.indirectpix.CelcoinIndirectPixOperations;
import com.brunopedraca.celcoin.cnab.CelcoinCnabClient;
import com.brunopedraca.celcoin.cnab.CelcoinCnabOperations;
import com.brunopedraca.celcoin.openfinance.CelcoinOpenFinanceClient;
import com.brunopedraca.celcoin.openfinance.CelcoinOpenFinanceOperations;
import com.brunopedraca.celcoin.jsr.CelcoinJsrClient;
import com.brunopedraca.celcoin.jsr.CelcoinJsrOperations;
import com.brunopedraca.celcoin.itp.CelcoinItpClient;
import com.brunopedraca.celcoin.itp.CelcoinItpOperations;
import com.brunopedraca.celcoin.topup.CelcoinTopupClient;
import com.brunopedraca.celcoin.topup.CelcoinTopupOperations;
import com.brunopedraca.celcoin.slc.CelcoinSlcClient;
import com.brunopedraca.celcoin.slc.CelcoinSlcOperations;
import com.brunopedraca.celcoin.reconciliation.CelcoinReconciliationClient;
import com.brunopedraca.celcoin.reconciliation.CelcoinReconciliationOperations;
import com.brunopedraca.celcoin.antifraud.CelcoinAntifraudClient;
import com.brunopedraca.celcoin.antifraud.CelcoinAntifraudOperations;
import com.brunopedraca.celcoin.mcp.CelcoinMcpProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties({CelcoinProperties.class, CelcoinCreditProperties.class, CelcoinEscrowProperties.class,
    CelcoinMcpProperties.class})
@ConditionalOnProperty(prefix = "celcoin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CelcoinAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    WebClient celcoinTokenWebClient(
            CelcoinProperties properties, ObjectProvider<CelcoinSslContextProvider> sslContextProvider) {
        return CelcoinWebClientFactory.create(properties, false, null, sslContextProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinTokenClient celcoinTokenClient(WebClient celcoinTokenWebClient, CelcoinProperties properties) {
        return new CelcoinTokenClient(celcoinTokenWebClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinTokenService celcoinTokenService(CelcoinTokenClient tokenClient, CelcoinProperties properties) {
        return new CelcoinTokenService(tokenClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "celcoin.ssl", name = "enabled", havingValue = "true")
    CelcoinSslContextProvider celcoinSslContextProvider(CelcoinProperties properties) {
        return new NettyCelcoinSslContextProvider(properties.ssl());
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinIdempotencyService celcoinIdempotencyService(
            CelcoinIdempotencyRecordRepository repository, ObjectMapper objectMapper) {
        return new CelcoinIdempotencyService(repository, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinHttpClient celcoinHttpClient(
            CelcoinProperties properties,
            CelcoinTokenService tokenService,
            ObjectProvider<CelcoinSslContextProvider> sslContextProvider,
            ObjectProvider<CelcoinIdempotencyService> idempotencyService) {
        return new CelcoinHttpClient(
                CelcoinWebClientFactory.create(properties, true, tokenService, sslContextProvider.getIfAvailable()),
                properties,
                idempotencyService.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinAcquiringOperations celcoinAcquiringOperations(CelcoinHttpClient httpClient) {
        return new CelcoinAcquiringClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinAccountOperations celcoinAccountOperations(CelcoinHttpClient httpClient) {
        return new CelcoinAccountClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinOnboardingOperations celcoinOnboardingOperations(CelcoinHttpClient httpClient) {
        return new CelcoinOnboardingClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinPixOperations celcoinPixOperations(CelcoinHttpClient httpClient) {
        return new CelcoinPixClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinPixAutoOperations celcoinPixAutoOperations(CelcoinHttpClient httpClient) {
        return new CelcoinPixAutoClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinSweepingOperations celcoinSweepingOperations(CelcoinHttpClient httpClient) {
        return new CelcoinSweepingClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinIndirectPixOperations celcoinIndirectPixOperations(CelcoinHttpClient httpClient) {
        return new CelcoinIndirectPixClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinCnabOperations celcoinCnabOperations(CelcoinHttpClient httpClient) {
        return new CelcoinCnabClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinOpenFinanceOperations celcoinOpenFinanceOperations(CelcoinHttpClient httpClient) {
        return new CelcoinOpenFinanceClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinJsrOperations celcoinJsrOperations(CelcoinHttpClient httpClient) {
        return new CelcoinJsrClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinItpOperations celcoinItpOperations(CelcoinHttpClient httpClient) {
        return new CelcoinItpClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinTopupOperations celcoinTopupOperations(CelcoinHttpClient httpClient) {
        return new CelcoinTopupClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinSlcOperations celcoinSlcOperations(ObjectMapper objectMapper) {
        return new CelcoinSlcClient(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinBoletoOperations celcoinBoletoOperations(CelcoinHttpClient httpClient) {
        return new CelcoinBoletoClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinCardOperations celcoinCardOperations(CelcoinHttpClient httpClient) {
        return new CelcoinCardClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinCreditOperations celcoinCreditOperations(CelcoinCreditProperties properties) {
        return new CelcoinCreditClient(WebClient.builder().build(), WebClient.builder().build(), properties);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinEscrowOperations celcoinEscrowOperations(CelcoinEscrowProperties properties) {
        return new CelcoinEscrowClient(WebClient.builder().build(), WebClient.builder().build(), properties);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinEmbeddedOperations celcoinEmbeddedOperations(CelcoinHttpClient httpClient) {
        return new CelcoinEmbeddedClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinReconciliationOperations celcoinReconciliationOperations(CelcoinHttpClient httpClient) {
        return new CelcoinReconciliationClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinAntifraudOperations celcoinAntifraudOperations(CelcoinHttpClient httpClient) {
        return new CelcoinAntifraudClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinVehicleOperations celcoinVehicleOperations(CelcoinHttpClient httpClient) {
        return new CelcoinVehicleClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinWebhookOperations celcoinWebhookOperations(CelcoinWebhookService service) {
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    CelcoinClient celcoinClient(
            CelcoinTokenService tokenService,
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
            CelcoinTopupOperations topups,
            CelcoinSlcOperations slc,
            CelcoinBoletoOperations boletos,
            CelcoinCardOperations cards,
            CelcoinWebhookOperations webhooks,
            CelcoinCreditOperations credit,
            CelcoinEscrowOperations escrow,
            CelcoinEmbeddedOperations embedded,
            CelcoinVehicleOperations vehicles,
            CelcoinReconciliationOperations reconciliation,
            CelcoinAntifraudOperations antifraud) {
        return new DefaultCelcoinClient(
                tokenService, acquiring, accounts, onboarding, pix, pixAuto, sweeping, indirectPix, cnab,
                openFinance, jsr, itp, topups, slc, boletos, cards, webhooks, credit, escrow, embedded, vehicles,
                reconciliation, antifraud);
    }
}
