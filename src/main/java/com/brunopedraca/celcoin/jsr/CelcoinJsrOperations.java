package com.brunopedraca.celcoin.jsr;

import com.brunopedraca.celcoin.jsr.CelcoinJsrDtos.*;
import java.util.Map;

/** Public operations for device binding and payments without redirect. */
public interface CelcoinJsrOperations {
    Map<String, Object> createEnrollment(EnrollmentRequest request, String idempotencyKey);

    Map<String, Object> processCallback(CallbackRequest request);

    Map<String, Object> fidoRegistrationOptions(String enrollmentId, FidoOptionsRequest request);

    Map<String, Object> registerFido(String enrollmentId, FidoRegistrationRequest request,
            String idempotencyKey);

    Map<String, Object> createPaymentInitiation(PaymentInitiationV4Request request,
            String idempotencyKey);

    Map<String, Object> fidoSignOptions(String enrollmentId, FidoSignOptionsRequest request);

    Map<String, Object> authorizeFido(String enrollmentId, FidoAuthorizationRequest request,
            String idempotencyKey);

    Map<String, Object> createPix(String paymentInitiationId, PixV4Request request,
            String idempotencyKey);

    Map<String, Object> createPaymentJourney(JourneySessionRequest request, String idempotencyKey);

    default Map<String, Object> createPaymentJourney(JourneySessionRequest request) {
        return createPaymentJourney(request, null);
    }

    Map<String, Object> listPaymentJourneys(JourneyPageRequest request);

    Map<String, Object> getPaymentJourney(String journeySessionId);

    Map<String, Object> listPaymentInitiations(Map<String, Object> query);

    Map<String, Object> getPaymentInitiation(String paymentInitiationId);

    /** Performs the non-cryptographic shape checks before passing a WebAuthn result to Celcoin. */
    FidoValidationResult validateFidoBiometry(Map<String, Object> assertion);
}
