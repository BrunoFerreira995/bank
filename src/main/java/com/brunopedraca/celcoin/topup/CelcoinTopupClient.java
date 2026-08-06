package com.brunopedraca.celcoin.topup;

import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.topup.TopupDtos.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CelcoinTopupClient implements CelcoinTopupOperations {
    private final CelcoinHttpClient httpClient;
    public CelcoinTopupClient(CelcoinHttpClient httpClient) { this.httpClient = httpClient; }

    @Override public ProviderListResponse listProviders(ProviderQuery request) {
        ensure();
        String path = "/baas/v2/topup/providers?" + query("stateCode", value(request, "stateCode"),
                "type", value(request, "type"), "category", value(request, "category"));
        Map<String, Object> raw = httpClient.get(path, Map.class, context(null));
        return new ProviderListResponse(parseProviders(raw), raw);
    }

    @Override public ProviderValuesResponse listValues(ProviderValuesQuery request) {
        ensure();
        Map<String, Object> raw = httpClient.get("/baas/v2/topup/provider-values?" + query(
                "stateCode", request.stateCode(), "providerId", request.providerId()), Map.class, context(null));
        return new ProviderValuesResponse(parseValues(raw), text(raw, "externalNsuQuery"),
                text(raw, "errorCode"), text(raw, "message"), integer(raw, "status"), raw);
    }

    @Override public TopupResponse reserve(TopupRequest request, String idempotencyKey) {
        ensure(); validate(request);
        Map<String, Object> body = new HashMap<>();
        body.put("account", request.account()); body.put("clientRequestId", request.clientRequestId());
        body.put("amount", request.amount()); body.put("providerId", request.providerId());
        if (request.signerCode() != null) body.put("signerCode", request.signerCode());
        if (request.tags() != null) body.put("tags", request.tags());
        if (request.topupData() != null) body.put("topupData", request.topupData());
        return parse(httpClient.post("/baas/v2/topup", body, Map.class, context(idempotencyKey)));
    }

    @Override public TopupResponse getStatus(String transactionId, String clientRequestId) {
        ensure();
        if (!StringUtils.hasText(transactionId) && !StringUtils.hasText(clientRequestId))
            throw new IllegalArgumentException("transactionId or clientRequestId is required");
        return parse(httpClient.get("/baas/v2/topup?" + query(
                "TransactionId", transactionId, "ClientRequestId", clientRequestId), Map.class, context(null)));
    }

    @Override public TopupResponse capture(String transactionId, CaptureRequest request, String idempotencyKey) {
        ensure();
        if (!StringUtils.hasText(transactionId)) throw new IllegalArgumentException("transactionId is required");
        return parse(httpClient.put("/v5/transactions/topups/" + encode(transactionId) + "/capture",
                request == null ? Map.of() : request, Map.class, context(idempotencyKey)));
    }

    private static void validate(TopupRequest r) {
        if (r == null) throw new IllegalArgumentException("topup request is required");
        if (!StringUtils.hasText(r.account())) throw new IllegalArgumentException("account is required");
        if (!StringUtils.hasText(r.clientRequestId())) throw new IllegalArgumentException("clientRequestId is required");
        if (r.providerId() == null) throw new IllegalArgumentException("providerId is required");
        if (r.amount() == null || r.amount().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be positive");
        if (r.account().length() > 20) throw new IllegalArgumentException("account must have at most 20 characters");
        if (r.clientRequestId().length() > 200) throw new IllegalArgumentException("clientRequestId must have at most 200 characters");
        if (r.tags() != null) {
            if (r.tags().size() > 20) throw new IllegalArgumentException("maximum of 20 tags");
            var keys = new HashSet<String>();
            for (TopupTag tag : r.tags()) {
                if (tag == null || !StringUtils.hasText(tag.key()) || !StringUtils.hasText(tag.value())) throw new IllegalArgumentException("tag key and value are required");
                if (!keys.add(tag.key())) throw new IllegalArgumentException("tag keys must be unique");
            }
        }
    }
    private TopupResponse parse(Map<String, Object> raw) {
        Map<String, Object> body = raw != null && raw.get("body") instanceof Map<?, ?> map ? cast(map) : raw;
        String transactionId = text(body, "id");
        if (transactionId == null) transactionId = text(body, "transactionId");
        return new TopupResponse(transactionId, text(body, "clientRequestId"), text(body, "status"), decimal(body, "amount"), integer(body, "providerId"), raw);
    }
    private List<Provider> parseProviders(Map<String, Object> raw) {
        List<Provider> result = new ArrayList<>(); Object values = raw == null ? null : raw.get("providers");
        if (values instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> map) {
            Map<String,Object> item = cast(map);
            result.add(new Provider(text(item,"name"), integer(item,"providerId"), integer(item,"category"),
                    integer(item,"TipoRecarganameProvider"), decimal(item,"minValue"), decimal(item,"maxValue"), item));
        }
        return result;
    }
    private List<ProviderValue> parseValues(Map<String, Object> raw) {
        List<ProviderValue> result = new ArrayList<>(); Object values = raw == null ? null : raw.get("value");
        if (values instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> map) {
            Map<String,Object> item = cast(map);
            result.add(new ProviderValue(text(item,"productName"), decimal(item,"cost"), decimal(item,"valueBonus"),
                    decimal(item,"minValue"), decimal(item,"maxValue"), integer(item,"code"), item));
        }
        return result;
    }
    private void ensure() { if (httpClient == null) throw new IllegalStateException("Celcoin topup client is not configured"); }
    private CelcoinRequestContext context(String key) { return CelcoinRequestContext.create(key); }
    private static Object value(ProviderQuery r, String n) { if (r == null) return null; return switch(n) { case "stateCode" -> r.stateCode(); case "type" -> r.type(); default -> r.category(); }; }
    private static String query(Object... values) { StringBuilder s=new StringBuilder(); for(int i=0;i<values.length;i+=2) if(values[i+1]!=null && StringUtils.hasText(String.valueOf(values[i+1]))) { if(!s.isEmpty())s.append('&'); s.append(values[i]).append('=').append(encode(String.valueOf(values[i+1]))); } return s.toString(); }
    private static String encode(String v) { return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8); }
    private static String text(Map<String,Object> m,String k){return m==null||m.get(k)==null?null:String.valueOf(m.get(k));}
    private static Integer integer(Map<String,Object> m,String k){try{return text(m,k)==null?null:Integer.valueOf(text(m,k));}catch(Exception e){return null;}}
    private static BigDecimal decimal(Map<String,Object> m,String k){try{return text(m,k)==null?null:new BigDecimal(text(m,k));}catch(Exception e){return null;}}
    @SuppressWarnings("unchecked") private static Map<String,Object> cast(Map<?,?> m){return (Map<String,Object>)m;}
}
