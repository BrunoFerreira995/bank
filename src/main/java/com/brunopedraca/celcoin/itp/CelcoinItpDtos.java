package com.brunopedraca.celcoin.itp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/** DTOs for Pix Instantâneo ITP (one consent per payment, redirect journey). */
public final class CelcoinItpDtos {
    private CelcoinItpDtos() {}

    public record ConsentRequest(@NotBlank String brandId, @NotBlank String redirectUrl,
            @NotBlank String loggedUserDocument, @NotBlank String creditorCpfCnpj,
            @NotBlank String creditorPersonType, @NotBlank String creditorName,
            @NotNull BigDecimal amount, @NotNull LocalDate date,
            @NotBlank String localInstrument, String proxy,
            @NotNull Map<String, Object> creditorAccount,
            Map<String, Object> debtorAccount) {}

    public record CallbackRequest(@NotBlank String code, @NotBlank String state,
            @NotBlank String idToken) {}

    public record PixRequest(Map<String, Object> data) {}

    public record WebhookData(String paymentInitiationId, String paymentId, String endToEndId,
            String previousStatus, String currentStatus, String rejectionReason,
            Map<String, Object> raw) {}

    public record WebhookEvent(String event, String timestamp, WebhookData data,
            Map<String, Object> raw) {}

    public record ErrorDescriptor(String code, String title, boolean retryable,
            String recommendation) {}
}
