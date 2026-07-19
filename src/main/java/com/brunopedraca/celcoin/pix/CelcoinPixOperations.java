package com.brunopedraca.celcoin.pix;

import com.brunopedraca.celcoin.pix.PixDtos.*;
import java.util.List;

public interface CelcoinPixOperations {
    default CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request) {
        return createQrCode(request, null);
    }

    CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request, String idempotencyKey);

    CelcoinPixStatusResponse getQrCodeStatus(String qrCodeId);

    List<CelcoinPixPaymentResponse> listReceipts(String accountId);

    default CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request) {
        return refund(request, null);
    }

    CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request, String idempotencyKey);

    CelcoinPixRefundResponse getRefund(String refundId);

    CelcoinPixKeyResponse lookupKey(String pixKey);

    CelcoinPixQrCodeResponse decodeEmv(String emv);

    default CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request) {
        return cashOut(request, null);
    }

    CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request, String idempotencyKey);

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
