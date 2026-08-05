package com.brunopedraca.celcoin.pixauto;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.pixauto.PixAutoDtos.*;
import java.util.List;

public class CelcoinPixAutoClient implements CelcoinPixAutoOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinPixAutoClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinPixAutoConsentResponse createConsent(CelcoinPixAutoConsentRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoConsentStatusResponse getConsentStatus(String consentId) {
        throw unspecified();
    }

    public CelcoinPixAutoConsentListResponse listConsents(CelcoinPixAutoListRequest request) {
        throw unspecified();
    }

    public CelcoinPixAutoConsentStatusResponse cancelConsent(String consentId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoScheduleResponse schedule(CelcoinPixAutoScheduleRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoScheduleStatusResponse getScheduleStatus(String scheduleId) {
        throw unspecified();
    }

    public CelcoinPixAutoScheduleListResponse listSchedules(CelcoinPixAutoListRequest request) {
        throw unspecified();
    }

    public CelcoinPixAutoCancelResponse cancelSchedule(String scheduleId, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoLiquidationResponse getLiquidation(String scheduleId) {
        throw unspecified();
    }

    public CelcoinPixAutoScheduleResponse createReceiveSchedule(
            CelcoinPixAutoReceiveScheduleRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoLiquidationResponse retryReceipt(CelcoinPixAutoRetryRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinPixAutoCancelResponse cancelRecurrence(String recurrenceId, String idempotencyKey) {
        throw unspecified();
    }

    public List<CelcoinPixAutoRejectionReason> rejectionReasons() {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin Pix Automático endpoint path is not configured because the official contract was not provided in this first version");
    }
}
