package com.brunopedraca.celcoin.boleto;

import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoListResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPeriodRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoAuthorizationRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoAuthorizationResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentRequest;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoPaymentResponse;
import com.brunopedraca.celcoin.boleto.BoletoDtos.CelcoinBoletoIssueRequest;

public interface CelcoinBoletoOperations {
    default CelcoinBoletoResponse issue(CelcoinBoletoRequest request) {
        return issue(request, null);
    }

    CelcoinBoletoResponse issue(CelcoinBoletoRequest request, String idempotencyKey);

    default CelcoinBoletoResponse issue(CelcoinBoletoIssueRequest request) {
        return issue(request, null);
    }

    CelcoinBoletoResponse issue(CelcoinBoletoIssueRequest request, String idempotencyKey);

    CelcoinBoletoResponse get(String boletoId);

    CelcoinBoletoAuthorizationResponse authorize(CelcoinBoletoAuthorizationRequest request);

    default CelcoinBoletoPaymentResponse pay(CelcoinBoletoPaymentRequest request) {
        return pay(request, null);
    }

    CelcoinBoletoPaymentResponse pay(CelcoinBoletoPaymentRequest request, String idempotencyKey);

    CelcoinBoletoPaymentResponse getPaymentStatus(String paymentId, String clientRequestId);

    CelcoinBoletoListResponse list(CelcoinBoletoPeriodRequest request);

    void cancel(String boletoId, String idempotencyKey);

    byte[] downloadPdf(String boletoId);
}
