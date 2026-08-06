package com.brunopedraca.celcoin.indirectpix;

import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.*;
import java.util.Map;

public interface CelcoinIndirectPixOperations {
    Map<String, Object> listKeys(CelcoinIndirectDictKeyListRequest request);

    Map<String, Object> lookupKey(CelcoinIndirectDictLookupRequest request);

    Map<String, Object> checkKeys(CelcoinIndirectDictKeyCheckRequest request);

    Map<String, Object> createKey(CelcoinIndirectDictKeyRequest request, String idempotencyKey);

    Map<String, Object> deleteKey(CelcoinIndirectDictDeleteRequest request, String idempotencyKey);

    Map<String, Object> createClaim(CelcoinIndirectClaimRequest request, String idempotencyKey);

    Map<String, Object> getClaim(String claimId);

    Map<String, Object> listClaims(Map<String, Object> query);

    Map<String, Object> createInfraction(CelcoinIndirectInfractionRequest request, String idempotencyKey);

    Map<String, Object> getInfraction(String infractionId);

    Map<String, Object> listInfractions(CelcoinIndirectInfractionListRequest request);

    Map<String, Object> closeInfraction(String infractionId, CelcoinIndirectInfractionCloseRequest request,
            String idempotencyKey);

    Map<String, Object> createMedRefund(CelcoinIndirectMedRefundRequest request, String idempotencyKey);

    Map<String, Object> getMedRefund(String refundId);

    Map<String, Object> cancelMedRefund(String refundId, String reason, String idempotencyKey);

    Map<String, Object> closeMedRefund(String refundId, CelcoinIndirectMedCloseRequest request, String idempotencyKey);

    Map<String, Object> createFundsRecovery(CelcoinFundsRecoveryRequest request, String idempotencyKey);

    Map<String, Object> getFundsRecovery(String fundsRecoveryId);

    Map<String, Object> cancelFundsRecovery(String fundsRecoveryId, String idempotencyKey);

    Map<String, Object> getFundsRecoveryGraph(String fundsRecoveryId);

    Map<String, Object> updateFundsRecovery(
            String fundsRecoveryId, CelcoinFundsRecoveryUpdateRequest request, String idempotencyKey);

    Map<String, Object> createPayment(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> getPaymentStatus(Map<String, Object> query);

    Map<String, Object> getReceivementStatus(Map<String, Object> query);

    Map<String, Object> reverseReceivement(String endToEndId, Map<String, Object> request, String idempotencyKey);

    Map<String, Object> createInternalReport(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> createStaticQrCode(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> createDynamicQrCode(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> decodeDynamicQrCode(String encodedUrl);

    CelcoinIndirectCashInAuthorizationResponse parseCashInAuthorization(Map<String, Object> payload);

    CelcoinIndirectWebhookEvent parseWebhook(Map<String, Object> payload);

    default Map<String, Object> closeMed2Infraction(
            String infractionReportId, CelcoinIndirectInfractionCloseRequest request, String idempotencyKey) {
        return closeInfraction(infractionReportId, request, idempotencyKey);
    }

    default Map<String, Object> createFundsRecoveryRefund(
            CelcoinIndirectMedRefundRequest request, String idempotencyKey) {
        return createMedRefund(request, idempotencyKey);
    }

    default Map<String, Object> closeFundsRecoveryRefund(
            String refundId, CelcoinIndirectMedCloseRequest request, String idempotencyKey) {
        return closeMedRefund(refundId, request, idempotencyKey);
    }
}
