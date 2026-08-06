package com.brunopedraca.celcoin.sweeping;

import com.brunopedraca.celcoin.sweeping.SweepingDtos.*;

public interface CelcoinSweepingOperations {
    CelcoinSweepingBrandListResponse listBrands();

    default CelcoinSweepingConsentResponse createConsent(CelcoinSweepingConsentRequest request) {
        return createConsent(request, null);
    }

    CelcoinSweepingConsentResponse createConsent(CelcoinSweepingConsentRequest request, String idempotencyKey);

    CelcoinSweepingCallbackResponse processCallback(CelcoinSweepingCallbackRequest request);

    default CelcoinSweepingConsentResponse cancelConsent(
            String paymentInitiationId, CelcoinSweepingCancelRequest request) {
        return cancelConsent(paymentInitiationId, request, null);
    }

    CelcoinSweepingConsentResponse cancelConsent(
            String paymentInitiationId, CelcoinSweepingCancelRequest request, String idempotencyKey);

    CelcoinSweepingConsentListResponse listConsents(CelcoinSweepingConsentListRequest request);

    CelcoinSweepingConsentResponse getConsent(String paymentInitiationId);

    default CelcoinSweepingPaymentResponse createPayment(CelcoinSweepingPaymentRequest request) {
        return createPayment(request, null);
    }

    CelcoinSweepingPaymentResponse createPayment(CelcoinSweepingPaymentRequest request, String idempotencyKey);
}
