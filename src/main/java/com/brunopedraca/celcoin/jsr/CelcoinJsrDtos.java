package com.brunopedraca.celcoin.jsr;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/** Payloads used by the Open Finance Jornada Sem Redirecionamento (JSR). */
public final class CelcoinJsrDtos {
    private CelcoinJsrDtos() {}

    public record EnrollmentRequest(Map<String, Object> data, @NotBlank String authorizationDevice,
            String tags) {}

    public record CallbackRequest(@NotBlank String code, @NotBlank String state,
            @NotBlank String idToken) {}

    public record PaymentInitiationV4Request(@NotBlank String brandId, @NotBlank String redirectUrl,
            Map<String, Object> enrollment, Map<String, Object> data, Boolean directoryCallback) {}

    public record FidoOptionsRequest(@NotBlank String rp, @NotBlank String platform) {}

    public record FidoRegistrationRequest(Map<String, Object> data) {}

    public record FidoSignOptionsRequest(@NotBlank String paymentInitiationId,
            @NotBlank String rp, @NotBlank String platform) {}

    public record FidoAuthorizationRequest(Map<String, Object> data, Boolean processPix) {}

    public record PixV4Request(Map<String, Object> data) {}

    public record FidoValidationResult(boolean valid, String reason) {}
}
