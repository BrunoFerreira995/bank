package com.brunopedraca.celcoin.itp;

import com.brunopedraca.celcoin.itp.CelcoinItpDtos.*;
import java.util.Map;

public interface CelcoinItpOperations {
    Map<String, Object> createConsent(ConsentRequest request, String idempotencyKey);

    default Map<String, Object> createConsent(ConsentRequest request) {
        return createConsent(request, null);
    }

    /** Semantic alias for a payment executed on the requested date (normally today). */
    default Map<String, Object> createInstantPayment(ConsentRequest request, String idempotencyKey) {
        return createConsent(request, idempotencyKey);
    }

    default Map<String, Object> createInstantPayment(ConsentRequest request) {
        return createInstantPayment(request, null);
    }

    /** Semantic alias for a payment scheduled on {@link ConsentRequest#date()}. */
    default Map<String, Object> createScheduledPayment(ConsentRequest request, String idempotencyKey) {
        return createConsent(request, idempotencyKey);
    }

    default Map<String, Object> createScheduledPayment(ConsentRequest request) {
        return createScheduledPayment(request, null);
    }

    Map<String, Object> processCallback(CallbackRequest request);

    Map<String, Object> getPaymentInitiation(String paymentInitiationId);

    default Map<String, Object> getPayment(String paymentInitiationId) {
        return getPaymentInitiation(paymentInitiationId);
    }

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
