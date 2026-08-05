package com.brunopedraca.celcoin.pix;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import com.brunopedraca.celcoin.pix.PixDtos.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.util.StringUtils;

public class CelcoinPixClient implements CelcoinPixOperations {
    private final CelcoinHttpClient httpClient;

    public CelcoinPixClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // ===================== QR Code / cobranças (cash-in) =====================

    @Override
    public CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request, String idempotencyKey) {
        return httpClient.post(
                "/pix/v1/brcode/dynamic", request, CelcoinPixQrCodeResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixCashInResponse createStaticChargeCashIn(
            CelcoinPixStaticChargeRequest request, String idempotencyKey) {
        return httpClient.post(
                "/pix/v1/brcode/static", request, CelcoinPixCashInResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixCashInResponse createDueDateQrCodeCashIn(
            CelcoinPixDueDateQrCodeRequest request, String idempotencyKey) {
        return httpClient.post(
                "/pix/v1/collection/duedate", request, CelcoinPixCashInResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixStaticChargeResponse getStaticCharge(
            String transactionIdBrCode, String transactionIdentification) {
        String path = "/pix/v1/brcode/static?"
                + query().param("transactionIdBrCode", transactionIdBrCode)
                        .param("transactionIdentification", transactionIdentification)
                        .build();
        return httpClient.get(path, CelcoinPixStaticChargeResponse.class, context(null));
    }

    @Override
    public CelcoinPixReceiptResponse getCashInReceipt(CelcoinPixReceiptRequest request) {
        String path = "/pix/v2/receivement/v2/status?"
                + query().param("endtoEnd", request.endToEndId())
                        .param("transactionId", request.transactionId())
                        .param("transactionIdBrCode", request.transactionIdBrCode())
                        .param("clientRequestId", request.clientRequestId())
                        .build();
        return httpClient.get(path, CelcoinPixReceiptResponse.class, context(null));
    }

    @Override
    public CelcoinPixMovementResponse getMovements(CelcoinPixMovementRequest request) {
        String path = "/baas/v2/wallet/movement?"
                + query().param("Account", request.account())
                        .param("DateFrom", date(request.dateFrom()))
                        .param("DateTo", date(request.dateTo()))
                        .param("LimitPerPage", request.limitPerPage())
                        .param("AdditionalInformation", request.additionalInformation())
                        .param("order", request.order())
                        .build();
        return httpClient.get(path, CelcoinPixMovementResponse.class, context(null));
    }

    // ===================== Devolução de cash-in =====================

    @Override
    public CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas-wallet-transactions-webservice/v1/pix/reverse",
                request,
                CelcoinPixRefundResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixRefundResponse getRefund(String returnIdentification) {
        return httpClient.get(
                "/baas-wallet-transactions-webservice/v1/pix/reverse/status?returnIdentification="
                        + encode(returnIdentification),
                CelcoinPixRefundResponse.class,
                context(null));
    }

    @Override
    public CelcoinPixDevolutionStatusResponse getDevolution(String returnIdentification) {
        return httpClient.get(
                "/pix/v2/receivement/v2/devolution/status?returnIdentification=" + encode(returnIdentification),
                CelcoinPixDevolutionStatusResponse.class,
                context(null));
    }

    // ===================== DICT =====================

    @Override
    public CelcoinPixKeyLookupResponse lookupKey(String account, String pixKey) {
        return httpClient.get(
                "/baas/v2/pix/dict/entry/external/" + encode(account) + "?key=" + encode(pixKey),
                CelcoinPixKeyLookupResponse.class,
                context(null));
    }

    // ===================== EMV =====================

    @Override
    public CelcoinPixEmvDecodeResponse decodeEmv(String emv) {
        return httpClient.post(
                "/pix/v1/emv/full",
                new CelcoinPixEmvDecodeRequest(emv, null, null),
                CelcoinPixEmvDecodeResponse.class,
                context(null));
    }

    // ===================== Cash-out / pagamentos =====================

    @Override
    public CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/payment", request, CelcoinPixPaymentResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixPaymentResponse cashOutToAccount(CelcoinPixCashOutAccountRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/payment",
                new CelcoinPixPaymentRequest(
                        request.amount(),
                        idempotencyKey == null ? UUID.randomUUID().toString() : idempotencyKey,
                        "MANUAL",
                        "IMMEDIATE",
                        "HIGH",
                        "TRANSFER",
                        null,
                        null,
                        request.description(),
                        new CelcoinPixDebitParty(request.sourceAccountId(), null, null, null, null),
                        new CelcoinPixCreditParty(
                                "30306294",
                                null,
                                request.targetAccount(),
                                request.targetBranch(),
                                request.targetDocument(),
                                request.targetName(),
                                "CACC"),
                        null),
                CelcoinPixPaymentResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixPaymentResponse cashOutByKey(CelcoinPixCashOutKeyRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/payment",
                new CelcoinPixPaymentRequest(
                        request.amount(),
                        idempotencyKey == null ? UUID.randomUUID().toString() : idempotencyKey,
                        "DICT",
                        "IMMEDIATE",
                        "HIGH",
                        "TRANSFER",
                        null,
                        null,
                        request.remittanceInformation(),
                        new CelcoinPixDebitParty(request.sourceAccountId(), null, null, null, null),
                        new CelcoinPixCreditParty(
                                request.bank(), request.key(), null, null, null, request.name(), "CACC"),
                        null),
                CelcoinPixPaymentResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixPaymentResponse cashOutStaticQrCode(
            CelcoinPixCashOutStaticQrCodeRequest request, String idempotencyKey) {
        return payQrCode(
                request.accountId(),
                request.emv(),
                request.amount(),
                request.description(),
                "STATIC_QRCODE",
                idempotencyKey);
    }

    @Override
    public CelcoinPixPaymentResponse cashOutDynamicQrCode(
            CelcoinPixCashOutDynamicQrCodeRequest request, String idempotencyKey) {
        return payQrCode(
                request.accountId(), request.emv(), null, request.description(), "DYNAMIC_QRCODE", idempotencyKey);
    }

    @Override
    public CelcoinPixStatusResponse getStatus(String id) {
        return httpClient.get(
                "/baas/v2/pix/payment/status?id=" + encode(id), CelcoinPixStatusResponse.class, context(null));
    }

    @Override
    public CelcoinPixStatusResponse getPaymentStatus(CelcoinPixPaymentStatusRequest request) {
        String path = "/baas/v2/pix/payment/status?"
                + query().param("id", request.id())
                        .param("endtoendId", request.endToEndId())
                        .param("clientCode", request.clientCode())
                        .build();
        return httpClient.get(path, CelcoinPixStatusResponse.class, context(null));
    }

    // ===================== Participantes =====================

    @Override
    public List<CelcoinPixParticipantResponse> participants() {
        CelcoinPixParticipantResponse[] array =
                httpClient.get("/pix/v1/participants", CelcoinPixParticipantResponse[].class, context(null));
        return array == null ? List.of() : List.of(array);
    }

    // ===================== Gerenciamento de chaves =====================

    @Override
    public CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/dict/entry", request, CelcoinPixKeyResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixKeyListResponse listKeys(String account) {
        return httpClient.get(
                "/baas/v2/pix/dict/entry/" + encode(account), CelcoinPixKeyListResponse.class, context(null));
    }

    @Override
    public CelcoinPixKeyOperationResponse deleteKey(CelcoinPixDeleteKeyRequest request, String idempotencyKey) {
        return httpClient.delete(
                "/baas/v2/pix/dict/entry/" + encode(request.key()),
                Map.of("account", request.account()),
                CelcoinPixKeyOperationResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixKeyUpdateResponse updateKeyName(CelcoinPixUpdateKeyRequest request, String idempotencyKey) {
        return httpClient.put(
                "/baas/v2/pix/dict/entry", request, CelcoinPixKeyUpdateResponse.class, context(idempotencyKey));
    }

    // ===================== Split =====================

    @Override
    public CelcoinPixSplitResponse createImmediateSplitQrCode(
            CelcoinPixImmediateSplitRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/immediate/split", request, CelcoinPixSplitResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixSplitResponse createDueDateSplitQrCode(
            CelcoinPixDueDateSplitRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/duedate/split", request, CelcoinPixSplitResponse.class, context(idempotencyKey));
    }

    // ===================== Agendamento =====================

    @Override
    public CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/payment",
                new CelcoinPixPaymentRequest(
                        request.amount(),
                        request.clientCode(),
                        "PIX_AUTOMATIC",
                        "SCHEDULED",
                        "NORMAL",
                        "TRANSFER",
                        request.transactionIdentification(),
                        null,
                        request.remittanceInformation(),
                        new CelcoinPixDebitParty(request.debitAccount(), null, null, null, null),
                        request.creditParty(),
                        new CelcoinPixScheduler(request.schedulerDate())),
                CelcoinPixScheduleResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixScheduleResponse getSchedule(String scheduleId) {
        return httpClient.get(
                "/baas/v2/scheduler/" + encode(scheduleId), CelcoinPixScheduleResponse.class, context(null));
    }

    @Override
    public CelcoinPixScheduleResponse cancelSchedule(String scheduleId, String idempotencyKey) {
        return httpClient.delete(
                "/baas/v2/scheduler/" + encode(scheduleId),
                null,
                CelcoinPixScheduleResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixScheduleListResponse listSchedules(CelcoinPixScheduleListRequest request) {
        String path = "/baas/v2/scheduler/listByAccount/" + encode(request.account()) + "?"
                + query().param("DateFrom", date(request.dateFrom()))
                        .param("DateTo", date(request.dateTo()))
                        .param("Page", request.page())
                        .param("LimitPerPage", request.limitPerPage())
                        .param("Status", request.status())
                        .build();
        return httpClient.get(path, CelcoinPixScheduleListResponse.class, context(null));
    }

    // ===================== Portabilidade / reivindicação =====================

    @Override
    public CelcoinPixClaimResponse claimKey(CelcoinPixClaimRequest request, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/dict/claim", request, CelcoinPixClaimResponse.class, context(idempotencyKey));
    }

    @Override
    public CelcoinPixClaimResponse confirmClaim(String id, String reason, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/dict/claim/confirm",
                new CelcoinPixClaimActionRequest(id, reason),
                CelcoinPixClaimResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixClaimResponse cancelClaim(String id, String reason, String idempotencyKey) {
        return httpClient.post(
                "/baas/v2/pix/dict/claim/cancel",
                new CelcoinPixClaimActionRequest(id, reason),
                CelcoinPixClaimResponse.class,
                context(idempotencyKey));
    }

    @Override
    public CelcoinPixClaimResponse getClaim(String id) {
        return httpClient.get("/baas/v2/pix/dict/claim/" + encode(id), CelcoinPixClaimResponse.class, context(null));
    }

    @Override
    public CelcoinPixClaimListResponse listClaims(CelcoinPixClaimListRequest request) {
        String path = "/baas/v2/pix/dict/claim/list?"
                + query().param("DateFrom", date(request.dateFrom()))
                        .param("DateTo", date(request.dateTo()))
                        .param("LimitPerPage", request.limitPerPage())
                        .param("Page", request.page())
                        .param("Status", request.status())
                        .param("claimType", request.claimType())
                        .build();
        return httpClient.get(path, CelcoinPixClaimListResponse.class, context(null));
    }

    // ===================== Internos =====================

    private CelcoinPixPaymentResponse payQrCode(
            String accountId,
            String emv,
            BigDecimal amount,
            String description,
            String initiationType,
            String idempotencyKey) {
        CelcoinPixEmvDecodeResponse decoded = decodeEmv(emv);
        if (decoded == null || decoded.key() == null) {
            throw new CelcoinIntegrationException(
                    "Não foi possível decodificar o EMV fornecido: nenhuma chave Pix encontrada");
        }
        CelcoinPixKeyLookupResponse lookup = lookupKey(accountId, decoded.key());
        BigDecimal paymentAmount = amount != null ? amount : decodedAmount(decoded);
        if (paymentAmount == null) {
            throw new CelcoinIntegrationException("Valor do Pix não informado e não disponível no EMV decodificado");
        }
        String bank = lookup.body() == null || lookup.body().account() == null
                ? null
                : lookup.body().account().participant();
        String name = lookup.body() == null || lookup.body().owner() == null
                ? null
                : lookup.body().owner().name();
        return httpClient.post(
                "/baas/v2/pix/payment",
                new CelcoinPixPaymentRequest(
                        paymentAmount,
                        idempotencyKey == null ? UUID.randomUUID().toString() : idempotencyKey,
                        initiationType,
                        "IMMEDIATE",
                        "HIGH",
                        "TRANSFER",
                        decoded.transactionIdentification(),
                        lookup.endToEndId(),
                        description,
                        new CelcoinPixDebitParty(accountId, null, null, null, null),
                        new CelcoinPixCreditParty(bank, decoded.key(), null, null, null, name, "CACC"),
                        null),
                CelcoinPixPaymentResponse.class,
                context(idempotencyKey));
    }

    private BigDecimal decodedAmount(CelcoinPixEmvDecodeResponse decoded) {
        if (decoded.body() == null || decoded.body().amount() == null) {
            return null;
        }
        BigDecimal finalAmount = decoded.body().amount().finalAmount();
        return finalAmount != null ? finalAmount : decoded.body().amount().original();
    }

    private CelcoinRequestContext context(String idempotencyKey) {
        return CelcoinRequestContext.create(idempotencyKey);
    }

    private static String encode(String value) {
        return StringUtils.hasText(value)
                ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                : "";
    }

    private static String date(LocalDate value) {
        return value == null ? null : value.toString();
    }

    private QueryBuilder query() {
        return new QueryBuilder();
    }

    private static final class QueryBuilder {
        private final StringBuilder sb = new StringBuilder();

        QueryBuilder param(String name, String value) {
            if (StringUtils.hasText(value)) {
                append(name, encode(value));
            }
            return this;
        }

        QueryBuilder param(String name, Object value) {
            if (value != null) {
                append(name, encode(String.valueOf(value)));
            }
            return this;
        }

        QueryBuilder param(String name, Boolean value) {
            if (value != null) {
                append(name, String.valueOf(value));
            }
            return this;
        }

        private void append(String name, String value) {
            if (!sb.isEmpty()) {
                sb.append('&');
            }
            sb.append(name).append('=').append(value);
        }

        String build() {
            return sb.toString();
        }
    }
}
