package com.brunopedraca.celcoin.pixauto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * DTOs mínimos do Pix Automático (pagamentos recorrentes). Os contratos oficiais
 * da Celcoin ainda não foram anexados ao projeto; quando forem, os campos podem
 * ser ajustados sem alterar a interface pública.
 */
public final class PixAutoDtos {
    private PixAutoDtos() {}

    // ------------------------------------------------------------------
    // Autorização / consentimento
    // ------------------------------------------------------------------

    public record CelcoinPixAutoConsentRequest(
            @NotBlank String accountId,
            @NotBlank String payerDocument,
            @NotBlank String payerName,
            String payerIspb,
            @NotNull BigDecimal maxAmount,
            String frequency,
            Integer dayOfMonth,
            Map<String, Object> metadata) {}

    public record CelcoinPixAutoConsentResponse(
            @JsonAlias("id") String consentId,
            String status,
            String paymentFlow,
            String authorizationCode,
            OffsetDateTime createdAt,
            String authorizationUrl,
            Map<String, Object> raw) {
        public CelcoinPixAutoConsentResponse(
                String consentId,
                String status,
                String paymentFlow,
                String authorizationCode,
                OffsetDateTime createdAt,
                Map<String, Object> raw) {
            this(consentId, status, paymentFlow, authorizationCode, createdAt, null, raw);
        }
    }

    public record CelcoinPixAutoConsentStatusResponse(
            String consentId,
            String status,
            String schedulerType,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Map<String, Object> raw) {}

    public record CelcoinPixAutoConsentListResponse(
            List<CelcoinPixAutoConsentStatusResponse> consents,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    // ------------------------------------------------------------------
    // Agendamento
    // ------------------------------------------------------------------

    public record CelcoinPixAutoScheduleRequest(
            @NotBlank String accountId,
            @NotBlank String consentId,
            @NotNull BigDecimal amount,
            LocalDate startDate,
            String frequency,
            Integer dayOfMonth,
            Map<String, Object> metadata) {}

    public record CelcoinPixAutoScheduleResponse(
            String scheduleId,
            String consentId,
            String status,
            BigDecimal amount,
            LocalDate startDate,
            OffsetDateTime createdAt,
            Map<String, Object> raw) {}

    public record CelcoinPixAutoScheduleStatusResponse(
            String scheduleId,
            String consentId,
            String status,
            BigDecimal amount,
            BigDecimal paidAmount,
            LocalDate nextExecutionDate,
            LocalDate lastExecutionDate,
            Integer retryCount,
            Map<String, Object> raw) {}

    public record CelcoinPixAutoScheduleListResponse(
            List<CelcoinPixAutoScheduleStatusResponse> schedules,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    // ------------------------------------------------------------------
    // Liquidação
    // ------------------------------------------------------------------

    public record CelcoinPixAutoLiquidationResponse(
            String scheduleId,
            String transactionId,
            BigDecimal amount,
            String status,
            OffsetDateTime settledAt,
            Map<String, Object> raw) {}

    // ------------------------------------------------------------------
    // Cancelamento e consultas
    // ------------------------------------------------------------------

    public record CelcoinPixAutoCancelRequest(@NotBlank String id, String reason, Map<String, Object> metadata) {}

    public record CelcoinPixAutoCancelResponse(
            String id, String status, OffsetDateTime cancelledAt, Map<String, Object> raw) {}

    public record CelcoinPixAutoListRequest(
            String accountId,
            String status,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            Integer page,
            Integer size) {}

    // ------------------------------------------------------------------
    // Jornada recebedora
    // ------------------------------------------------------------------

    public record CelcoinPixAutoReceiveScheduleRequest(
            @NotBlank String accountId,
            @NotBlank String consentId,
            @NotNull BigDecimal amount,
            LocalDate startDate,
            String frequency,
            Integer dayOfMonth,
            Map<String, Object> metadata) {}

    public record CelcoinPixAutoRetryRequest(
            @NotBlank String scheduleId, Integer attempt, String reason, LocalDate date,
            String endToEndId, String originalRecurringPaymentId, Map<String, Object> metadata) {
        public CelcoinPixAutoRetryRequest(
                String scheduleId, Integer attempt, String reason, Map<String, Object> metadata) {
            this(scheduleId, attempt, reason, null, null, null, metadata);
        }
    }

    public record CelcoinPixAutoRejectionReason(String code, String description) {}

    public record CelcoinPixAutoCallbackRequest(
            @NotBlank String code, @NotBlank String state, String idToken, Map<String, Object> metadata) {}

    public record CelcoinPixAutoCallbackResponse(
            String consentId, String status, String recurringConsentId, Map<String, Object> raw) {}
}
