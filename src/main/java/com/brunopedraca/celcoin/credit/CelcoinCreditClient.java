package com.brunopedraca.celcoin.credit;

import com.brunopedraca.celcoin.common.exception.CelcoinAuthenticationException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class CelcoinCreditClient implements CelcoinCreditOperations {
    private final WebClient apiClient;
    private final WebClient authClient;
    private final CelcoinCreditProperties properties;
    private final AtomicReference<CelcoinCreditTokenResponse> token = new AtomicReference<>();

    public CelcoinCreditClient(
            WebClient apiClient, WebClient authClient, CelcoinCreditProperties properties) {
        this.apiClient = apiClient;
        this.authClient = authClient;
        this.properties = properties;
    }

    @Override
    public CelcoinCreditTokenResponse authenticate() {
        CelcoinCreditTokenResponse current = token.get();
        if (current != null && current.usable()) return current;
        if (properties.clientId() == null || properties.clientSecret() == null) {
            throw new CelcoinAuthenticationException("celcoin.credit.client-id and client-secret are required");
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
                throw new CelcoinAuthenticationException("Credit token response did not include access_token");
            }
            CelcoinCreditTokenResponse refreshed = new CelcoinCreditTokenResponse(
                    String.valueOf(response.get("access_token")),
                    response.get("token_type") == null ? "Bearer" : String.valueOf(response.get("token_type")),
                    number(response.get("expires_in")), Instant.now());
            token.set(refreshed);
            return refreshed;
        } catch (CelcoinAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new CelcoinAuthenticationException("Unable to authenticate with Celcoin Credit");
        }
    }

    @Override
    public Map<String, Object> createApplication(Map<String, Object> request) {
        return post("/originator/applications", request);
    }

    @Override
    public Map<String, Object> listApplications(CelcoinCreditApplicationQuery query) {
        StringBuilder path = new StringBuilder("/banking/originator/applications?");
        param(path, "page", query.page()); param(path, "size", query.size());
        param(path, "borrower_id", query.borrowerId()); param(path, "product_id", query.productId());
        param(path, "status", query.status()); param(path, "external_id", query.externalId());
        param(path, "taxpayer_id", query.taxpayerId()); param(path, "sequential_id", query.sequentialId());
        param(path, "created_date_from", query.createdDateFrom()); param(path, "created_date_to", query.createdDateTo());
        return get(path.toString());
    }

    @Override public Map<String, Object> getApplication(String applicationId) {
        return get("/banking/originator/applications/" + encode(applicationId));
    }

    @Override public Map<String, Object> simulate(String productId, Map<String, Object> request) {
        return post("/banking/originator/products/" + encode(productId) + "/preview", request);
    }

    @Override public Map<String, Object> listCustomVariables() { return get("/banking/originator/custom-variables"); }
    @Override public Map<String, Object> createCustomVariable(Map<String, Object> request) {
        return post("/banking/originator/custom-variables", request);
    }
    @Override public Map<String, Object> updateCustomVariable(String id, Map<String, Object> request) {
        return put("/banking/originator/custom-variables/" + encode(id), request);
    }
    @Override public void deleteCustomVariable(String id) {
        authenticated().delete().uri(properties.apiBaseUrl() + "/banking/originator/custom-variables/" + encode(id))
                .retrieve().toBodilessEntity().block();
    }
    @Override public Map<String, Object> getSignatures(String applicationId) {
        return get("/banking/originator/applications/" + encode(applicationId) + "/signatures");
    }
    @Override public Map<String, Object> sendTimestampSignature(String applicationId, Map<String, Object> request) {
        return post("/banking/originator/applications/" + encode(applicationId) + "/signatures", request);
    }

    @Override
    public Map<String, Object> simulatePortability(String productId, Map<String, Object> request) {
        return simulate(productId, request);
    }

    @Override
    public Map<String, Object> requestPortabilityAuthorization(Map<String, Object> request) {
        return post("/banking/originator/guarantee/authorization-permission", request);
    }

    @Override
    public Map<String, Object> authorizePortabilityEmploymentQuery(Map<String, Object> request) {
        return post("/banking/originator/guarantee/authorization", request);
    }

    @Override
    public Map<String, Object> getPortabilityEmploymentBalance(String productId, String taxpayerId) {
        return get("/banking/originator/guarantee/" + encode(productId) + "/get-balance?taxpayer_id="
                + encode(taxpayerId));
    }

    @Override
    public Map<String, Object> createPortabilityBundle(Map<String, Object> request) {
        return post("/banking/originator/application-bundles", request);
    }

    @Override
    public Map<String, Object> getPortabilityBundle(String bundleId) {
        return get("/banking/originator/application-bundles/" + encode(bundleId));
    }

    @Override
    public byte[] downloadPortabilityAuthorization(String taxpayerId) {
        return authenticated().get()
                .uri(properties.apiBaseUrl() + "/banking/originator/guarantee/acceptterm/download?taxpayerId="
                        + encode(taxpayerId))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    @Override
    public Map<String, Object> createPerson(Map<String, Object> request) {
        return post("/banking/originator/persons", request);
    }

    @Override
    public Map<String, Object> getPerson(String personId) {
        return get("/banking/originator/persons/" + encode(personId));
    }

    @Override
    public Map<String, Object> updatePerson(String personId, Map<String, Object> request) {
        return put("/banking/originator/persons/" + encode(personId), request);
    }

    @Override
    public Map<String, Object> createBusiness(Map<String, Object> request) {
        return post("/banking/originator/business", request);
    }

    @Override
    public Map<String, Object> getBusiness(String businessId) {
        return get("/banking/originator/business/" + encode(businessId));
    }

    @Override
    public Map<String, Object> updateBusiness(String businessId, Map<String, Object> request) {
        return put("/banking/originator/business/" + encode(businessId), request);
    }

    @Override
    public Map<String, Object> createBusinessRelation(String businessId, Map<String, Object> request) {
        return post("/banking/originator/business/" + encode(businessId) + "/relations", request);
    }

    @Override
    public Map<String, Object> getQualification(String productId, String applicationId) {
        return get("/banking/originator/products/" + encode(productId) + "/qualification-requests?application_id="
                + encode(applicationId));
    }

    @Override
    public Map<String, Object> qualifyApplication(
            String productId, String applicationId, Map<String, Object> request) {
        return post("/banking/originator/products/" + encode(productId) + "/qualification-requests/"
                + encode(applicationId), request);
    }

    @Override
    public Map<String, Object> removeGuarantee(String applicationId, String reason) {
        return post("/banking/originator/applications/" + encode(applicationId)
                + "/remove/guarantee?reason=" + encode(reason), Map.of());
    }

    @Override
    public Map<String, Object> createWebhook(Map<String, Object> request) {
        return post("/banking/originator/webhooks", request);
    }

    @Override
    public Map<String, Object> listWebhooks() {
        return get("/banking/originator/webhooks");
    }

    @Override
    public Map<String, Object> updateWebhook(String webhookId, Map<String, Object> request) {
        return put("/banking/originator/webhooks/" + encode(webhookId), request);
    }

    @Override
    public void deleteWebhook(String webhookId) {
        authenticated().delete()
                .uri(properties.apiBaseUrl() + "/banking/originator/webhooks/" + encode(webhookId))
                .retrieve().toBodilessEntity().block();
    }

    @Override
    public Map<String, Object> submitWorkersCreditOffer(String proposalId, Map<String, Object> offer) {
        return post("/banking/originator/workers-credit/proposal/" + encode(proposalId), offer);
    }

    @Override
    public Map<String, Object> getFgtsBalance(String productId, String taxpayerId) {
        return get("/banking/originator/guarantee/" + encode(productId) + "/get-balance?taxpayer_id="
                + encode(taxpayerId));
    }

    @Override
    public Map<String, Object> listGuaranteeEvents(Map<String, Object> filters) {
        StringBuilder path = new StringBuilder("/banking/originator/guarantee-events?");
        if (filters != null) {
            filters.forEach((key, value) -> param(path, key, value));
        }
        return get(path.toString());
    }

    @Override
    public Map<String, Object> getGuaranteeStatus(String applicationId) {
        return get("/banking/originator/applications/" + encode(applicationId) + "/guarantee");
    }

    @Override
    public CelcoinCreditTokenResponse authenticateArmyConsigned() {
        return authenticate();
    }

    @Override
    public Map<String, Object> getArmyConsignedMargin(String productId, String taxpayerId) {
        return get("/banking/originator/guarantee/" + encode(productId) + "/get-balance?taxpayer_id="
                + encode(taxpayerId));
    }

    @Override
    public Map<String, Object> simulateArmyConsignedCcb(String productId, Map<String, Object> request) {
        return simulate(productId, request);
    }

    @Override
    public Map<String, Object> createArmyConsignedBorrower(Map<String, Object> request) {
        return createPerson(request);
    }

    @Override
    public Map<String, Object> createArmyConsignedPurchaseBundle(Map<String, Object> request) {
        return post("/banking/originator/application-bundles", request);
    }

    @Override
    public Map<String, Object> getArmyConsignedPurchaseBundle(String bundleId) {
        return get("/banking/originator/application-bundles/" + encode(bundleId));
    }

    @Override
    public Map<String, Object> getArmyConsignedOperationStatus(String applicationId) {
        return getApplication(applicationId);
    }

    private Map<String, Object> get(String path) { return authenticated().get().uri(properties.apiBaseUrl() + path)
            .retrieve().bodyToMono(Map.class).block(); }
    private Map<String, Object> post(String path, Object body) { return authenticated().post().uri(properties.apiBaseUrl() + path)
            .contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(Map.class).block(); }
    private Map<String, Object> put(String path, Object body) { return authenticated().put().uri(properties.apiBaseUrl() + path)
            .contentType(MediaType.APPLICATION_JSON).bodyValue(body).retrieve().bodyToMono(Map.class).block(); }
    private WebClient authenticated() { return apiClient.mutate().defaultHeaders(h -> h.setBearerAuth(authenticate().accessToken())).build(); }
    private static Long number(Object v) { return v instanceof Number n ? n.longValue() : Long.valueOf(String.valueOf(v)); }
    private static String encode(Object v) { return java.net.URLEncoder.encode(String.valueOf(v), StandardCharsets.UTF_8); }
    private static void param(StringBuilder b, String key, Object value) { if (value != null) { if (b.charAt(b.length()-1) != '?') b.append('&'); b.append(key).append('=').append(encode(value)); } }
}
