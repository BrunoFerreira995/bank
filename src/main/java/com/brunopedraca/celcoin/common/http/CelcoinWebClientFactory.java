package com.brunopedraca.celcoin.common.http;

import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

public final class CelcoinWebClientFactory {
    private CelcoinWebClientFactory() {}

    public static WebClient create(CelcoinProperties properties, boolean authenticated, CelcoinTokenService tokenService) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.connectTimeout().toMillis())
                .responseTimeout(properties.readTimeout());

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(properties.baseUrl() == null ? "" : properties.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (authenticated) {
            builder.filter(bearerFilter(tokenService));
        }
        return builder.build();
    }

    private static ExchangeFilterFunction bearerFilter(CelcoinTokenService tokenService) {
        return (request, next) -> next.exchange(org.springframework.web.reactive.function.client.ClientRequest.from(request)
                .headers(headers -> headers.setBearerAuth(tokenService.getAccessToken()))
                .build());
    }
}
