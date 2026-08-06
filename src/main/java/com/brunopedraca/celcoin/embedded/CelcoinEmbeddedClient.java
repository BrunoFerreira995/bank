package com.brunopedraca.celcoin.embedded;

import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CelcoinEmbeddedClient implements CelcoinEmbeddedOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinEmbeddedClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override public Map<String, Object> celcoinAccountBalance(String account) {
        return get("/baas/v2/wallet/balance?Account=" + encode(account), null);
    }

    @Override public Map<String, Object> ddaRegister(Map<String, Object> request, String key) {
        return post("/dda-subscription-webservice/v1/subscription/Register", request, key);
    }
    @Override public Map<String, Object> ddaDelete(Map<String, Object> request, String key) {
        return delete("/dda-subscription-webservice/v1/subscription/Register", request, key);
    }
    @Override public Map<String, Object> ddaRegisterInvoices(Map<String, Object> request, String key) {
        return post("/dda-serviceinvoice-webservice/v1/invoice/register", request, key);
    }
    @Override public Map<String, Object> ddaRegisterWebhook(Map<String, Object> request) {
        return post("/dda-servicewebhook-webservice/v1/webhook/register", request, null);
    }
    @Override public Map<String, Object> ddaListWebhooks() {
        return get("/dda-servicewebhook-webservice/v1/webhook/routes", null);
    }

    @Override public Map<String, Object> billAuthorize(Map<String, Object> request, String key) {
        return post("/v5/transactions/billpayments/authorize", request, key);
    }
    @Override public Map<String, Object> billReserve(Map<String, Object> request, String key) {
        return post("/v5/transactions/billpayments", request, key);
    }
    @Override public Map<String, Object> billCapture(String transactionId, Map<String, Object> request, String key) {
        return put("/v5/transactions/billpayments/" + encode(transactionId) + "/capture", request, key);
    }
    @Override public Map<String, Object> billReverse(String transactionId, String key) {
        return delete("/v5/transactions/billpayments/" + encode(transactionId) + "/reverse", Map.of(), key);
    }
    @Override public Map<String, Object> billStatus(Map<String, Object> filters) {
        return get("/v5/transactions/status-consult?" + query(filters), null);
    }
    @Override public Map<String, Object> billOccurrences(Map<String, Object> filters) {
        return get("/v5/transactions/occurrency?" + query(filters), null);
    }
    @Override public Map<String, Object> billInstitutions() {
        return get("/v5/transactions/institutions", null);
    }

    @Override public Map<String, Object> nfseCreateCompany(Map<String, Object> request, String key) {
        return post("/invoices/v1/company", request, key);
    }
    @Override public Map<String, Object> nfseGetCompany(String companyId) {
        return get("/invoices/v1/company/" + encode(companyId), null);
    }
    @Override public Map<String, Object> nfseRegister(Map<String, Object> request, String key) {
        return post("/invoices/v1/nfse", request, key);
    }
    @Override public Map<String, Object> nfseGet(String serviceInvoiceId) {
        return get("/nfse/" + encode(serviceInvoiceId), null);
    }
    @Override public Map<String, Object> nfseCancel(Map<String, Object> request, String key) {
        return post("/nfse/cancel", request, key);
    }

    @Override public Map<String, Object> tedTransfer(Map<String, Object> request, String key) {
        return post("/baas/v1/transfer", request, key);
    }
    @Override public Map<String, Object> tedStatus(Map<String, Object> filters) {
        return get("/baas/v1/transfer/status?" + query(filters), null);
    }
    @Override public Map<String, Object> reconciliation(String resource, Map<String, Object> filters) {
        return get("/reconciliation/v1/" + encode(resource) + "?" + query(filters), null);
    }

    private Map<String, Object> get(String path, String key) {
        return httpClient.get(path, Map.class, context(key));
    }
    private Map<String, Object> post(String path, Object body, String key) {
        return httpClient.post(path, body, Map.class, context(key));
    }
    private Map<String, Object> put(String path, Object body, String key) {
        return httpClient.put(path, body, Map.class, context(key));
    }
    private Map<String, Object> delete(String path, Object body, String key) {
        return httpClient.delete(path, body, Map.class, context(key));
    }
    private static CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static String encode(Object value) { return java.net.URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8); }
    private static String query(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        filters.forEach((key, value) -> { if (value != null) { if (!result.isEmpty()) result.append('&'); result.append(encode(key)).append('=').append(encode(value)); } });
        return result.toString();
    }
}
