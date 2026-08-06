package com.brunopedraca.celcoin.itp;

import com.brunopedraca.celcoin.itp.CelcoinItpDtos.*;
import java.util.Map;

public interface CelcoinItpOperations {
    Map<String, Object> createConsent(ConsentRequest request, String idempotencyKey);

    default Map<String, Object> createConsent(ConsentRequest request) {
        return createConsent(request, null);
    }

    Map<String, Object> processCallback(CallbackRequest request);

    Map<String, Object> getPaymentInitiation(String paymentInitiationId);

    Map<String, Object> createPix(String paymentInitiationId, PixRequest request,
            String idempotencyKey);

    default Map<String, Object> createPix(String paymentInitiationId, PixRequest request) {
        return createPix(paymentInitiationId, request, null);
    }

    WebhookEvent parseWebhook(Map<String, Object> payload);

    ItpStateMachine.ConsentState consentState(String status);

    ItpStateMachine.PaymentState paymentState(String status);

    ErrorDescriptor paymentError(String code);
}
