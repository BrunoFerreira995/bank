package com.brunopedraca.celcoin.pix;

import com.brunopedraca.celcoin.pix.PixDtos.*;
import java.util.List;

public interface CelcoinPixOperations {
    default CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request) {
        return createQrCode(request, null);
    }

    CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request, String idempotencyKey);

    CelcoinPixStatusResponse getQrCodeStatus(String qrCodeId);

    default CelcoinPixCashInResponse createAccountCashIn(CelcoinPixCashInAccountRequest request) {
        return createAccountCashIn(request, null);
    }

    CelcoinPixCashInResponse createAccountCashIn(CelcoinPixCashInAccountRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createRandomKeyCashIn(CelcoinPixCashInKeyRequest request) {
        return createRandomKeyCashIn(request, null);
    }

    CelcoinPixCashInResponse createRandomKeyCashIn(CelcoinPixCashInKeyRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createIndividualKeyCashIn(CelcoinPixCashInKeyRequest request) {
        return createIndividualKeyCashIn(request, null);
    }

    CelcoinPixCashInResponse createIndividualKeyCashIn(CelcoinPixCashInKeyRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createStaticChargeCashIn(CelcoinPixCashInStaticChargeRequest request) {
        return createStaticChargeCashIn(request, null);
    }

    CelcoinPixCashInResponse createStaticChargeCashIn(
            CelcoinPixCashInStaticChargeRequest request, String idempotencyKey);

    default CelcoinPixCashInResponse createDueDateQrCodeCashIn(CelcoinPixCashInDueDateQrCodeRequest request) {
        return createDueDateQrCodeCashIn(request, null);
    }

    CelcoinPixCashInResponse createDueDateQrCodeCashIn(
            CelcoinPixCashInDueDateQrCodeRequest request, String idempotencyKey);

    List<CelcoinPixPaymentResponse> listReceipts(String accountId);

    List<CelcoinPixCashInReceiptResponse> listCashInReceipts(String accountId);

    default CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request) {
        return refund(request, null);
    }

    CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request, String idempotencyKey);

    CelcoinPixRefundResponse getRefund(String refundId);

    default CelcoinPixCashInCautionaryBlockResponse createCashInCautionaryBlock(
            CelcoinPixCashInCautionaryBlockRequest request) {
        return createCashInCautionaryBlock(request, null);
    }

    CelcoinPixCashInCautionaryBlockResponse createCashInCautionaryBlock(
            CelcoinPixCashInCautionaryBlockRequest request, String idempotencyKey);

    CelcoinPixKeyResponse lookupKey(String pixKey);

    CelcoinPixQrCodeResponse decodeEmv(String emv);

    default CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request) {
        return cashOut(request, null);
    }

    CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutToAccount(CelcoinPixCashOutAccountRequest request) {
        return cashOutToAccount(request, null);
    }

    CelcoinPixPaymentResponse cashOutToAccount(CelcoinPixCashOutAccountRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutStaticQrCode(CelcoinPixCashOutStaticQrCodeRequest request) {
        return cashOutStaticQrCode(request, null);
    }

    CelcoinPixPaymentResponse cashOutStaticQrCode(CelcoinPixCashOutStaticQrCodeRequest request, String idempotencyKey);

    default CelcoinPixPaymentResponse cashOutDynamicQrCode(CelcoinPixCashOutDynamicQrCodeRequest request) {
        return cashOutDynamicQrCode(request, null);
    }

    CelcoinPixPaymentResponse cashOutDynamicQrCode(
            CelcoinPixCashOutDynamicQrCodeRequest request, String idempotencyKey);

    CelcoinPixCashOutTransferListResponse listCashOutTransfers(CelcoinPixCashOutTransferListRequest request);

    default CelcoinPixCashOutCautionaryBlockResponse createCashOutCautionaryBlock(
            CelcoinPixCashOutCautionaryBlockRequest request) {
        return createCashOutCautionaryBlock(request, null);
    }

    CelcoinPixCashOutCautionaryBlockResponse createCashOutCautionaryBlock(
            CelcoinPixCashOutCautionaryBlockRequest request, String idempotencyKey);

    CelcoinPixStatusResponse getStatus(String transactionId);

    List<CelcoinPixParticipantResponse> participants();

    default CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request) {
        return createKey(request, null);
    }

    CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request, String idempotencyKey);

    List<CelcoinPixKeyResponse> listKeys(String accountId);

    void deleteKey(String keyId, String idempotencyKey);

    CelcoinPixKeyResponse updateKeyName(String keyId, String name, String idempotencyKey);

    default CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request) {
        return schedule(request, null);
    }

    CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request, String idempotencyKey);

    CelcoinPixScheduleResponse getSchedule(String scheduleId);

    void cancelSchedule(String scheduleId, String idempotencyKey);

    List<CelcoinPixScheduleResponse> listSchedules(String accountId);

    CelcoinPixQrCodeResponse createImmediateSplitQrCode(CelcoinPixSplitRequest request, String idempotencyKey);

    CelcoinPixQrCodeResponse createDueDateSplitQrCode(CelcoinPixSplitRequest request, String idempotencyKey);
}
