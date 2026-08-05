package com.brunopedraca.celcoin.pix;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class PixDtos {
    private PixDtos() {}

    public record CelcoinPixQrCodeRequest(BigDecimal amount, String description, Map<String, Object> metadata) {}

    public record CelcoinPixQrCodeResponse(String qrCodeId, String emv, String status, Map<String, Object> raw) {}

    public record CelcoinPixCashInAccountRequest(
            @NotBlank String branch,
            @NotBlank String account,
            BigDecimal amount,
            String description,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashInKeyRequest(
            @NotBlank String accountId,
            String keyType,
            String key,
            BigDecimal amount,
            String description,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashInStaticChargeRequest(
            @NotBlank String accountId,
            BigDecimal amount,
            String description,
            String payerDocument,
            String payerName,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashInDueDateQrCodeRequest(
            @NotBlank String accountId,
            BigDecimal amount,
            OffsetDateTime dueAt,
            String description,
            String payerDocument,
            String payerName,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashInResponse(
            String cashInId,
            String transactionId,
            String qrCodeId,
            String emv,
            String status,
            Map<String, Object> raw) {}

    public record CelcoinPixCashInReceiptResponse(
            String cashInId,
            String transactionId,
            String accountId,
            BigDecimal amount,
            String status,
            OffsetDateTime receivedAt,
            Map<String, Object> raw) {}

    public record CelcoinPixCashInWebhookEvent(
            String eventId,
            String eventType,
            String cashInId,
            String transactionId,
            String accountId,
            BigDecimal amount,
            String status,
            OffsetDateTime occurredAt,
            Map<String, Object> raw) {}

    public record CelcoinPixCashInCautionaryBlockRequest(
            @NotBlank String transactionId, String reason, Map<String, Object> metadata) {}

    public record CelcoinPixCashInCautionaryBlockResponse(
            String blockId, String transactionId, String status, Map<String, Object> raw) {}

    public record CelcoinPixPaymentRequest(
            @NotBlank String accountId, @NotBlank String pixKey, BigDecimal amount, String description) {}

    public record CelcoinPixPaymentResponse(String transactionId, String status, Map<String, Object> raw) {}

    public record CelcoinPixCashOutAccountRequest(
            @NotBlank String sourceAccountId,
            @NotBlank String targetBranch,
            @NotBlank String targetAccount,
            String targetDocument,
            String targetName,
            BigDecimal amount,
            String description,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashOutStaticQrCodeRequest(
            @NotBlank String accountId,
            @NotBlank String emv,
            BigDecimal amount,
            String description,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashOutDynamicQrCodeRequest(
            @NotBlank String accountId, @NotBlank String emv, String description, Map<String, Object> metadata) {}

    public record CelcoinPixCashOutTransferListRequest(
            String accountId,
            String status,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            Integer page,
            Integer size) {}

    public record CelcoinPixCashOutTransferListResponse(
            List<CelcoinPixPaymentResponse> transfers,
            Integer page,
            Integer size,
            Long total,
            Map<String, Object> raw) {}

    public record CelcoinPixCashOutWebhookEvent(
            String eventId,
            String eventType,
            String transactionId,
            String accountId,
            BigDecimal amount,
            String status,
            OffsetDateTime occurredAt,
            Map<String, Object> raw) {}

    public record CelcoinPixCashOutCautionaryBlockRequest(
            @NotBlank String transactionId, String reason, Map<String, Object> metadata) {}

    public record CelcoinPixCashOutCautionaryBlockResponse(
            String blockId, String transactionId, String status, Map<String, Object> raw) {}

    public record CelcoinPixStatusResponse(
            String transactionId, String status, OffsetDateTime updatedAt, Map<String, Object> raw) {}

    public record CelcoinPixRefundRequest(@NotBlank String transactionId, BigDecimal amount, String reason) {}

    public record CelcoinPixRefundResponse(String refundId, String status, Map<String, Object> raw) {}

    public record CelcoinPixKeyRequest(@NotBlank String accountId, @NotBlank String keyType, String key) {}

    public record CelcoinPixKeyResponse(
            String keyId, String keyType, String key, String status, Map<String, Object> raw) {}

    public record CelcoinPixScheduleRequest(CelcoinPixPaymentRequest payment, OffsetDateTime scheduledAt) {}

    public record CelcoinPixScheduleResponse(String scheduleId, String status, Map<String, Object> raw) {}

    public record CelcoinPixParticipantResponse(String ispb, String name, Map<String, Object> raw) {}

    public record CelcoinPixSplitRequest(CelcoinPixQrCodeRequest qrCode, List<Map<String, Object>> splits) {}
}
