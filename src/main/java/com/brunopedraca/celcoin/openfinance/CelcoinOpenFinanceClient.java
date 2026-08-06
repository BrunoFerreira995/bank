package com.brunopedraca.celcoin.openfinance;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.openfinance.OpenFinanceDtos.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CelcoinOpenFinanceClient implements CelcoinOpenFinanceOperations {
    private static final String DATA_BASE = "/baas/v1/open/dat";
    private final CelcoinHttpClient httpClient;

    public CelcoinOpenFinanceClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, Object> listBrands() {
        ensureConfigured();
        return httpClient.get("/baas/v1/open/itp/participants/brands?type=DATA", Map.class, context(null));
    }

    @Override
    public Map<String, Object> getBrand(String brandId) {
        ensureConfigured();
        return httpClient.get("/baas/v1/open/itp/participants/brands/" + encode(brandId), Map.class, context(null));
    }

    @Override
    public Map<String, Object> createConsent(CelcoinOpenFinanceConsentRequest request, String idempotencyKey) {
        ensureConfigured();
        Map<String, Object> data = request.data() == null ? new HashMap<>() : new HashMap<>(request.data());
        data.put("brandId", request.brandId());
        return httpClient.post("/baas/v1/open/dat/consents", Map.of("data", data), Map.class,
                context(idempotencyKey));
    }

    @Override
    public Map<String, Object> processCallback(CelcoinOpenFinanceCallbackRequest request) {
        ensureConfigured();
        return httpClient.post("/baas/v1/open/dat/consents/callback", Map.of(
                        "code", request.code(), "state", request.state(), "id_token", request.idToken()),
                Map.class, context(null));
    }

    @Override
    public Map<String, Object> listResources(CelcoinOpenFinancePageRequest request) {
        ensureConfigured();
        return httpClient.get("/api/open-keys/resources/v3/resources?" + pageQuery(request), Map.class, context(null));
    }

    @Override
    public Map<String, Object> getData(String resourcePath, CelcoinOpenFinancePageRequest request) {
        ensureConfigured();
        if (!StringUtils.hasText(resourcePath) || !resourcePath.startsWith("/baas/v1/open/dat/")) {
            throw new IllegalArgumentException("resourcePath must be an allowed /baas/v1/open/dat path");
        }
        return httpClient.get(resourcePath + (resourcePath.contains("?") ? "&" : "?") + pageQuery(request),
                Map.class, context(null));
    }

    @Override public Map<String, Object> personalIdentifications(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/personal/identifications", r);
    }
    @Override public Map<String, Object> personalQualifications(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/personal/qualifications", r);
    }
    @Override public Map<String, Object> personalFinancialRelations(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/personal/financial-relations", r);
    }
    @Override public Map<String, Object> businessIdentifications(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/business/identifications", r);
    }
    @Override public Map<String, Object> businessQualifications(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/business/qualifications", r);
    }
    @Override public Map<String, Object> businessFinancialRelations(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/customers/business/financial-relations", r);
    }
    @Override public Map<String, Object> accounts(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/accounts", r);
    }
    @Override public Map<String, Object> accountTransactions(String id, CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/accounts/" + encode(id) + "/transactions", r);
    }
    @Override public Map<String, Object> loans(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/loans/contracts", r);
    }
    @Override public Map<String, Object> financings(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/financings/contracts", r);
    }
    @Override public Map<String, Object> creditCardAccounts(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/credit-cards-accounts", r);
    }
    @Override public Map<String, Object> bankFixedIncome(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/bank-fixed-incomes/investments", r);
    }
    @Override public Map<String, Object> creditFixedIncome(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/credit-fixed-incomes/investments", r);
    }
    @Override public Map<String, Object> variableIncome(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/variable-incomes/investments", r);
    }
    @Override public Map<String, Object> treasury(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/treasury-titles/investments", r);
    }
    @Override public Map<String, Object> funds(CelcoinOpenFinancePageRequest r) {
        return getData(DATA_BASE + "/funds/investments", r);
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException(
                "Celcoin Open Finance endpoint path is not configured because the official contract was not provided");
    }
    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static String encode(String value) {
        return StringUtils.hasText(value) ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";
    }
    private static String pageQuery(CelcoinOpenFinancePageRequest request) {
        if (request == null) return "";
        StringBuilder query = new StringBuilder();
        param(query, "page", request.page());
        param(query, "page-size", request.pageSize());
        param(query, "pagination-key", request.paginationKey());
        return query.toString();
    }
    private static void param(StringBuilder query, String name, Object value) {
        if (value != null && StringUtils.hasText(String.valueOf(value))) {
            if (!query.isEmpty()) query.append('&');
            query.append(name).append('=').append(encode(String.valueOf(value)));
        }
    }
}
