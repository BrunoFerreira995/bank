package com.brunopedraca.celcoin.boleto;

import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoListResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPeriodRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;

public class CelcoinBoletoClient implements CelcoinBoletoOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinBoletoClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinBoletoResponse issue(CelcoinBoletoRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinBoletoResponse get(String boletoId) {
        throw unspecified();
    }

    public CelcoinBoletoListResponse list(CelcoinBoletoPeriodRequest request) {
        throw unspecified();
    }

    public void cancel(String boletoId, String idempotencyKey) {
        throw unspecified();
    }

    public byte[] downloadPdf(String boletoId) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin boleto endpoint path is not configured because the official contract was not provided in this first version");
    }
}
