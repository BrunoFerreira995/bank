package com.brunopedraca.celcoin.pix;

import com.brunopedraca.celcoin.pix.PixDtos.*;
import java.util.List;

public interface CelcoinPixOperations {
    // ===================== QR Code / cobranças (cash-in) =====================

    default CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request) {
        return createQrCode(request, null);
    }

    CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createStaticChargeCashIn(CelcoinPixStaticChargeRequest request) {
        return createStaticChargeCashIn(request, null);
    }

    CelcoinPixCashInResponse createStaticChargeCashIn(CelcoinPixStaticChargeRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createDueDateQrCodeCashIn(CelcoinPixDueDateQrCodeRequest request) {
        return createDueDateQrCodeCashIn(request, null);
    }

    CelcoinPixCashInResponse createDueDateQrCodeCashIn(CelcoinPixDueDateQrCodeRequest request, String idempotencyKey);

    CelcoinPixStaticChargeResponse getStaticCharge(String transactionIdBrCode, String transactionIdentification);

    CelcoinPixReceiptResponse getCashInReceipt(CelcoinPixReceiptRequest request);

    CelcoinPixMovementResponse getMovements(CelcoinPixMovementRequest request);

    // ===================== Devolução de cash-in =====================

    default CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request) {
        return refund(request, null);
    }

    CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request, String idempotencyKey);

    CelcoinPixRefundResponse getRefund(String returnIdentification);

    CelcoinPixDevolutionStatusResponse getDevolution(String returnIdentification);

    // ===================== DICT =====================

    CelcoinPixKeyLookupResponse lookupKey(String account, String pixKey);

    // ===================== EMV =====================

    CelcoinPixEmvDecodeResponse decodeEmv(String emv);

    // ===================== Cash-out / pagamentos =====================

    default CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request) {
        return cashOut(request, null);
    }

    CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutToAccount(CelcoinPixCashOutAccountRequest request) {
        return cashOutToAccount(request, null);
    }

    CelcoinPixPaymentResponse cashOutToAccount(CelcoinPixCashOutAccountRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutByKey(CelcoinPixCashOutKeyRequest request) {
        return cashOutByKey(request, null);
    }

    CelcoinPixPaymentResponse cashOutByKey(CelcoinPixCashOutKeyRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutStaticQrCode(CelcoinPixCashOutStaticQrCodeRequest request) {
        return cashOutStaticQrCode(request, null);
    }

    CelcoinPixPaymentResponse cashOutStaticQrCode(CelcoinPixCashOutStaticQrCodeRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutDynamicQrCode(CelcoinPixCashOutDynamicQrCodeRequest request) {
        return cashOutDynamicQrCode(request, null);
    }

    CelcoinPixPaymentResponse cashOutDynamicQrCode(
            CelcoinPixCashOutDynamicQrCodeRequest request, String idempotencyKey);

    CelcoinPixStatusResponse getStatus(String id);

    CelcoinPixStatusResponse getPaymentStatus(CelcoinPixPaymentStatusRequest request);

    // ===================== Participantes =====================

    List<CelcoinPixParticipantResponse> participants();

    // ===================== Gerenciamento de chaves =====================

    default CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request) {
        return createKey(request, null);
    }

    CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request, String idempotencyKey);

    CelcoinPixKeyListResponse listKeys(String account);

    CelcoinPixKeyOperationResponse deleteKey(CelcoinPixDeleteKeyRequest request, String idempotencyKey);

    default CelcoinPixKeyOperationResponse deleteKey(CelcoinPixDeleteKeyRequest request) {
        return deleteKey(request, null);
    }

    CelcoinPixKeyUpdateResponse updateKeyName(CelcoinPixUpdateKeyRequest request, String idempotencyKey);

    default CelcoinPixKeyUpdateResponse updateKeyName(CelcoinPixUpdateKeyRequest request) {
        return updateKeyName(request, null);
    }

    // ===================== Split =====================

    CelcoinPixSplitResponse createImmediateSplitQrCode(CelcoinPixImmediateSplitRequest request, String idempotencyKey);

    default CelcoinPixSplitResponse createImmediateSplitQrCode(CelcoinPixImmediateSplitRequest request) {
        return createImmediateSplitQrCode(request, null);
    }

    CelcoinPixSplitResponse createDueDateSplitQrCode(CelcoinPixDueDateSplitRequest request, String idempotencyKey);

    default CelcoinPixSplitResponse createDueDateSplitQrCode(CelcoinPixDueDateSplitRequest request) {
        return createDueDateSplitQrCode(request, null);
    }

    // ===================== Agendamento =====================

    default CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request) {
        return schedule(request, null);
    }

    CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request, String idempotencyKey);

    CelcoinPixScheduleResponse getSchedule(String scheduleId);

    CelcoinPixScheduleResponse cancelSchedule(String scheduleId, String idempotencyKey);

    default CelcoinPixScheduleResponse cancelSchedule(String scheduleId) {
        return cancelSchedule(scheduleId, null);
    }

    CelcoinPixScheduleListResponse listSchedules(CelcoinPixScheduleListRequest request);

    // ===================== Portabilidade / reivindicação =====================

    default CelcoinPixClaimResponse claimKey(CelcoinPixClaimRequest request) {
        return claimKey(request, null);
    }

    CelcoinPixClaimResponse claimKey(CelcoinPixClaimRequest request, String idempotencyKey);

    CelcoinPixClaimResponse confirmClaim(String id, String reason, String idempotencyKey);

    CelcoinPixClaimResponse cancelClaim(String id, String reason, String idempotencyKey);

    CelcoinPixClaimResponse getClaim(String id);

    CelcoinPixClaimListResponse listClaims(CelcoinPixClaimListRequest request);
}
