package com.brunopedraca.celcoin.topup;

import com.brunopedraca.celcoin.topup.TopupDtos.*;
import java.util.List;

public interface CelcoinTopupOperations {
    ProviderListResponse listProviders(ProviderQuery request);
    ProviderValuesResponse listValues(ProviderValuesQuery request);
    default TopupResponse reserve(TopupRequest request) { return reserve(request, null); }
    TopupResponse reserve(TopupRequest request, String idempotencyKey);
    TopupResponse getStatus(String transactionId, String clientRequestId);
    TopupResponse capture(String transactionId, CaptureRequest request, String idempotencyKey);
    default List<CelcoinTopupErrors.Error> errors() { return CelcoinTopupErrors.all(); }
}
