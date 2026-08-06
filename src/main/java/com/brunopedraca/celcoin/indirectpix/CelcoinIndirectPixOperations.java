package com.brunopedraca.celcoin.indirectpix;

import com.brunopedraca.celcoin.indirectpix.IndirectPixDtos.*;
import java.util.Map;

public interface CelcoinIndirectPixOperations {
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
}
