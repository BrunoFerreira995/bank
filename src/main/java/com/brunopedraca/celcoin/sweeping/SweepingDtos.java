package com.brunopedraca.celcoin.sweeping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class SweepingDtos {
    private SweepingDtos() {}

    public record CelcoinSweepingBrand(String id, String name, String status, Map<String, Object> raw) {}

    public record CelcoinSweepingBrandListResponse(List<CelcoinSweepingBrand> brands, Map<String, Object> raw) {}

    public record CelcoinSweepingCreditor(
            @NotBlank String name, @NotBlank String cpfCnpj, @NotBlank String personType) {}

    public record CelcoinSweepingConsentRequest(
            @NotBlank String brandId,
            @NotBlank String redirectUrl,
            @NotBlank String loggedUserDocument,
            @NotEmpty List<CelcoinSweepingCreditor> creditors,
            @NotNull Map<String, Object> sweepingConfiguration,
            Map<String, Object> metadata) {}

    public record CelcoinSweepingConsentResponse(
            String id,
            String authorizationUrl,
            String status,
            String ofConsentId,
            String journeySessionId,
            Map<String, Object> raw) {}

    public record CelcoinSweepingCallbackRequest(
            @NotBlank String code, @NotBlank String state, @NotBlank String idToken) {}

    public record CelcoinSweepingCallbackResponse(
            String consentId, String status, String ofConsentId, Map<String, Object> raw) {}

    public record CelcoinSweepingConsentListRequest(
            String status, LocalDate initialDate, LocalDate finalDate, Integer page, Integer pageSize) {}

    public record CelcoinSweepingConsentListResponse(
            List<CelcoinSweepingConsentResponse> consents,
            Integer page,
            Integer pageSize,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinSweepingCancelRequest(@NotBlank String cancelledByDocument, String reason) {}

    public record CelcoinSweepingPaymentRequest(
            @NotBlank String paymentInitiationId,
            @NotNull LocalDate date,
            @NotNull BigDecimal amount,
            @NotNull Map<String, Object> creditorAccount,
            String remittanceInformation,
            String ibgeTownCode,
            Map<String, Object> riskSignals) {}

    public record CelcoinSweepingPaymentResponse(
            String id, String status, String endToEndId, BigDecimal amount, Map<String, Object> raw) {}
}
