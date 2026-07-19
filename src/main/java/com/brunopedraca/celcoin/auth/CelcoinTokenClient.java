package com.brunopedraca.celcoin.auth;

import com.brunopedraca.celcoin.common.exception.CelcoinAuthenticationException;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class CelcoinTokenClient {
    private final WebClient webClient;
    private final CelcoinProperties properties;

    public CelcoinTokenClient(WebClient webClient, CelcoinProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public CelcoinTokenResponse generateToken() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("grant_type", "client_credentials");
        try {
            CelcoinTokenResponse response = webClient
                    .post()
                    .uri(properties.tokenPath())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> clientResponse.releaseBody()
                            .thenReturn(new CelcoinAuthenticationException(
                                    "Unable to authenticate with Celcoin", clientResponse.statusCode(), null)))
                    .bodyToMono(CelcoinTokenResponse.class)
                    .block(properties.readTimeout().plusSeconds(5));
            if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
                throw new CelcoinAuthenticationException("Celcoin token response did not include an access token");
            }
            return response.withObtainedAt(Instant.now());
        } catch (CelcoinAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new CelcoinAuthenticationException("Unable to authenticate with Celcoin");
        }
    }
}
