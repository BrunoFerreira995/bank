package com.brunopedraca.celcoin.escrow;

import com.brunopedraca.celcoin.common.exception.CelcoinAuthenticationException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class CelcoinEscrowClient implements CelcoinEscrowOperations {
    private static final String API = "/v1";

    private final WebClient apiClient;
    private final WebClient authClient;
    private final CelcoinEscrowProperties properties;
    private final AtomicReference<CelcoinEscrowTokenResponse> token = new AtomicReference<>();

    public CelcoinEscrowClient(WebClient apiClient, WebClient authClient, CelcoinEscrowProperties properties) {
        this.apiClient = apiClient;
        this.authClient = authClient;
        this.properties = properties;
    }

    @Override
    public CelcoinEscrowTokenResponse authenticate() {
        CelcoinEscrowTokenResponse current = token.get();
        if (current != null && current.usable()) return current;
        if (properties.clientId() == null || properties.clientSecret() == null
                || properties.clientId().isBlank() || properties.clientSecret().isBlank()) {
            throw new CelcoinAuthenticationException("celcoin.escrow.client-id and client-secret are required");
        }
        String basic = Base64.getEncoder().encodeToString(
                (properties.clientId() + ":" + properties.clientSecret()).getBytes(StandardCharsets.UTF_8));
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");
        try {
            Map<?, ?> response = authClient.post().uri(properties.authUrl())
                    .header("Authorization", "Basic " + basic)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(form).retrieve().bodyToMono(Map.class).block();
            if (response == null || response.get("access_token") == null) {
                throw new CelcoinAuthenticationException("Escrow token response did not include access_token");
            }
            CelcoinEscrowTokenResponse refreshed = new CelcoinEscrowTokenResponse(
                    String.valueOf(response.get("access_token")),
                    response.get("token_type") == null ? "Bearer" : String.valueOf(response.get("token_type")),
                    number(response.get("expires_in")), Instant.now());
            token.set(refreshed);
            return refreshed;
        } catch (CelcoinAuthenticationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CelcoinAuthenticationException("Unable to authenticate with Celcoin Escrow");
        }
    }

    @Override public Map<String, Object> requestDocumentUpload(Map<String, Object> request) { return post(API + "/documents/upload", request); }
    @Override public Map<String, Object> createPerson(Map<String, Object> request) { return post(API + "/persons", request); }
    @Override public Map<String, Object> createAccount(Map<String, Object> request) { return post(API + "/accounts", request); }
    @Override public Map<String, Object> getAccount(String id) { return get(API + "/accounts/" + encode(id)); }
    @Override public Map<String, Object> getBalance(String id) { return get(API + "/accounts/" + encode(id) + "/balance"); }
    @Override public Map<String, Object> getStatement(String id, Map<String, Object> filters) { return get(API + "/accounts/" + encode(id) + "/statement?" + query(filters)); }
    @Override public Map<String, Object> createDestination(String id, Map<String, Object> request) { return post(API + "/accounts/" + encode(id) + "/destinations", request); }
    @Override public Map<String, Object> listDestinations(String id) { return get(API + "/accounts/" + encode(id) + "/destinations"); }
    @Override public Map<String, Object> updateDestination(String id, String destinationId, Map<String, Object> request) { return patch(API + "/accounts/" + encode(id) + "/destinations/" + encode(destinationId), request); }
    @Override public void deleteDestination(String id, String destinationId) { delete(API + "/accounts/" + encode(id) + "/destinations/" + encode(destinationId)); }
    @Override public Map<String, Object> createPosting(Map<String, Object> request) { return post(API + "/postings", request); }
    @Override public Map<String, Object> listPostings(Map<String, Object> filters) { return get(API + "/postings?" + query(filters)); }
    @Override public Map<String, Object> reviewPosting(String postingId, Map<String, Object> request) { return put(API + "/postings/" + encode(postingId) + "/review", request); }
    @Override public Map<String, Object> cancelPosting(String postingId, String reason) { return post(API + "/postings/" + encode(postingId) + "/cancel", Map.of("reason", reason)); }
    @Override public Map<String, Object> createWallet(Map<String, Object> request) { return post(API + "/wallets", request); }
    @Override public Map<String, Object> listWallets(String accountId) { return get(API + "/wallets?accountId=" + encode(accountId)); }
    @Override public Map<String, Object> updateWallet(String walletId, Map<String, Object> request) { return put(API + "/wallets/" + encode(walletId), request); }
    @Override public void archiveWallet(String walletId) { delete(API + "/wallets/" + encode(walletId)); }
    @Override public Map<String, Object> createCharge(String walletId, Map<String, Object> request) { return post(API + "/wallets/" + encode(walletId) + "/charges", request); }
    @Override public Map<String, Object> listCharges(String walletId, Map<String, Object> filters) { return get(API + "/wallets/" + encode(walletId) + "/charges?" + query(filters)); }
    @Override public void deleteCharge(String walletId, String chargeId) { delete(API + "/wallets/" + encode(walletId) + "/charges/" + encode(chargeId)); }
    @Override public Map<String, Object> createDepositRetention(String accountId, Map<String, Object> request) { return post(API + "/accounts/" + encode(accountId) + "/deposit-retention", request); }
    @Override public Map<String, Object> getDepositRetention(String accountId) { return get(API + "/accounts/" + encode(accountId) + "/deposit-retention"); }
    @Override public Map<String, Object> createWebhookConfiguration(String accountId, Map<String, Object> request) { return post(API + "/accounts/" + encode(accountId) + "/webhook-configurations", request); }
    @Override public Map<String, Object> listWebhookConfigurations(String accountId) { return get(API + "/accounts/" + encode(accountId) + "/webhook-configurations"); }
    @Override public void deleteWebhookConfiguration(String accountId, String webhookId) { delete(API + "/accounts/" + encode(accountId) + "/webhook-configurations/" + encode(webhookId)); }

    private Map<String, Object> get(String path) { return authenticated().get().uri(properties.apiBaseUrl() + path).retrieve().bodyToMono(Map.class).block(); }
    private Map<String, Object> post(String path, Object body) { return authenticated().post().uri(properties.apiBaseUrl() + path).contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(Map.class).block(); }
    private Map<String, Object> put(String path, Object body) { return authenticated().put().uri(properties.apiBaseUrl() + path).contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(Map.class).block(); }
    private Map<String, Object> patch(String path, Object body) { return authenticated().patch().uri(properties.apiBaseUrl() + path).contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(Map.class).block(); }
    private void delete(String path) { authenticated().delete().uri(properties.apiBaseUrl() + path).retrieve().toBodilessEntity().block(); }
    private WebClient authenticated() { return apiClient.mutate().defaultHeaders(h -> h.setBearerAuth(authenticate().accessToken())).build(); }
    private static Long number(Object value) { return value instanceof Number n ? n.longValue() : Long.valueOf(String.valueOf(value)); }
    private static String encode(Object value) { return java.net.URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8); }
    private static String query(Map<String, Object> filters) { if (filters == null || filters.isEmpty()) return ""; StringBuilder result = new StringBuilder(); filters.forEach((k, v) -> { if (v != null) { if (!result.isEmpty()) result.append('&'); result.append(encode(k)).append('=').append(encode(v)); } }); return result.toString(); }
}
