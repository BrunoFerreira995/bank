package com.brunopedraca.celcoin.pix;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.pix.PixDtos.*;
import java.util.List;

public class CelcoinPixClient implements CelcoinPixOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinPixClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinPixQrCodeResponse createQrCode(CelcoinPixQrCodeRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixStatusResponse getQrCodeStatus(String qrCodeId) {
        throw unspecified();
    }

    public CelcoinPixCashInResponse createAccountCashIn(CelcoinPixCashInAccountRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixCashInResponse createRandomKeyCashIn(CelcoinPixCashInKeyRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixCashInResponse createIndividualKeyCashIn(CelcoinPixCashInKeyRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixCashInResponse createStaticChargeCashIn(
            CelcoinPixCashInStaticChargeRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixCashInResponse createDueDateQrCodeCashIn(
            CelcoinPixCashInDueDateQrCodeRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public List<CelcoinPixPaymentResponse> listReceipts(String accountId) {
        throw unspecified();
    }

    public List<CelcoinPixCashInReceiptResponse> listCashInReceipts(String accountId) {
        throw unspecified();
    }

    public CelcoinPixRefundResponse refund(CelcoinPixRefundRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixRefundResponse getRefund(String refundId) {
        throw unspecified();
    }

    public CelcoinPixCashInCautionaryBlockResponse createCashInCautionaryBlock(
            CelcoinPixCashInCautionaryBlockRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixKeyResponse lookupKey(String pixKey) {
        throw unspecified();
    }

    public CelcoinPixQrCodeResponse decodeEmv(String emv) {
        throw unspecified();
    }

    public CelcoinPixPaymentResponse cashOut(CelcoinPixPaymentRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixStatusResponse getStatus(String transactionId) {
        throw unspecified();
    }

    public List<CelcoinPixParticipantResponse> participants() {
        throw unspecified();
    }

    public CelcoinPixKeyResponse createKey(CelcoinPixKeyRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public List<CelcoinPixKeyResponse> listKeys(String accountId) {
        throw unspecified();
    }

    public void deleteKey(String keyId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixKeyResponse updateKeyName(String keyId, String name, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixScheduleResponse schedule(CelcoinPixScheduleRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixScheduleResponse getSchedule(String scheduleId) {
        throw unspecified();
    }

    public void cancelSchedule(String scheduleId, String idempotencyKey) {
        throw unspecified();
    }

    public List<CelcoinPixScheduleResponse> listSchedules(String accountId) {
        throw unspecified();
    }

    public CelcoinPixQrCodeResponse createImmediateSplitQrCode(CelcoinPixSplitRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixQrCodeResponse createDueDateSplitQrCode(CelcoinPixSplitRequest request, String idempotencyKey) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin Pix endpoint path is not configured because the official contract was not provided in this first version");
    }
}
