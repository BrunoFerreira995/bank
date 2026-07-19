package com.brunopedraca.celcoin.boleto;

import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoListResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPeriodRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;

public interface CelcoinBoletoOperations {
    default CelcoinBoletoResponse issue(CelcoinBoletoRequest request) {
        return issue(request, null);
    }

    CelcoinBoletoResponse issue(CelcoinBoletoRequest request, String idempotencyKey);

    CelcoinBoletoResponse get(String boletoId);

    CelcoinBoletoListResponse list(CelcoinBoletoPeriodRequest request);

    void cancel(String boletoId, String idempotencyKey);

    byte[] downloadPdf(String boletoId);
}
