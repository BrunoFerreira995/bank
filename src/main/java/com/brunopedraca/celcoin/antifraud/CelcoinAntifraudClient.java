package com.brunopedraca.celcoin.antifraud;

import com.brunopedraca.celcoin.antifraud.CelcoinAntifraudDtos.RiskAssessment;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CelcoinAntifraudClient implements CelcoinAntifraudOperations {
    private static final String BASE = "https://apicorp.celcoin.com.br/ftm/v1/query/assess";
    private final CelcoinHttpClient httpClient;

    public CelcoinAntifraudClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public RiskAssessment assess(String taxId) {
        return assess(taxId, false);
    }

    @Override
    public RiskAssessment assess(String taxId, boolean verbose) {
        if (taxId == null || !taxId.matches("\\d{11}|\\d{14}")) {
            throw new IllegalArgumentException("taxId must contain 11 or 14 digits");
        }
        String path = BASE + "?taxid=" + URLEncoder.encode(taxId, StandardCharsets.UTF_8)
                + (verbose ? "&verbose=true" : "");
        return httpClient.get(path, RiskAssessment.class, CelcoinRequestContext.create(null));
    }
}
