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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@EnableConfigurationProperties(CelcoinProperties.class)
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
            CelcoinBoletoOperations boletos,
            CelcoinCardOperations cards,
            CelcoinWebhookOperations webhooks) {
        return new DefaultCelcoinClient(
                tokenService, acquiring, accounts, onboarding, pix, pixAuto, boletos, cards, webhooks);
    }
}
