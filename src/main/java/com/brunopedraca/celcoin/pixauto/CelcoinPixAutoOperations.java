package com.brunopedraca.celcoin.pixauto;

import com.brunopedraca.celcoin.pixauto.PixAutoDtos.*;
import java.util.List;

/**
 * Operações do Pix Automático (jornadas pagadora e recebedora): consentimento,
 * agendamento, liquidação, cancelamento e consultas.
 */
public interface CelcoinPixAutoOperations {
    // Autorização / consentimento

    default CelcoinPixAutoConsentResponse createConsent(CelcoinPixAutoConsentRequest request) {
        return createConsent(request, null);
    }

    CelcoinPixAutoConsentResponse createConsent(CelcoinPixAutoConsentRequest request, String idempotencyKey);

    CelcoinPixAutoConsentStatusResponse getConsentStatus(String consentId);

    CelcoinPixAutoConsentListResponse listConsents(CelcoinPixAutoListRequest request);

    default CelcoinPixAutoConsentStatusResponse cancelConsent(String consentId) {
        return cancelConsent(consentId, null);
    }

    CelcoinPixAutoConsentStatusResponse cancelConsent(String consentId, String idempotencyKey);

    CelcoinPixAutoCallbackResponse processCallback(CelcoinPixAutoCallbackRequest request);

    // Agendamento

    default CelcoinPixAutoScheduleResponse schedule(CelcoinPixAutoScheduleRequest request) {
        return schedule(request, null);
    }

    CelcoinPixAutoScheduleResponse schedule(CelcoinPixAutoScheduleRequest request, String idempotencyKey);

    CelcoinPixAutoScheduleStatusResponse getScheduleStatus(String scheduleId);

    CelcoinPixAutoScheduleListResponse listSchedules(CelcoinPixAutoListRequest request);

    default CelcoinPixAutoCancelResponse cancelSchedule(String scheduleId) {
        return cancelSchedule(scheduleId, null);
    }

    CelcoinPixAutoCancelResponse cancelSchedule(String scheduleId, String idempotencyKey);

    // Liquidação

    CelcoinPixAutoLiquidationResponse getLiquidation(String scheduleId);

    // Jornada recebedora

    default CelcoinPixAutoScheduleResponse createReceiveSchedule(CelcoinPixAutoReceiveScheduleRequest request) {
        return createReceiveSchedule(request, null);
    }

    CelcoinPixAutoScheduleResponse createReceiveSchedule(
            CelcoinPixAutoReceiveScheduleRequest request, String idempotencyKey);

    default CelcoinPixAutoLiquidationResponse retryReceipt(CelcoinPixAutoRetryRequest request) {
        return retryReceipt(request, null);
    }

    CelcoinPixAutoLiquidationResponse retryReceipt(CelcoinPixAutoRetryRequest request, String idempotencyKey);

    default CelcoinPixAutoCancelResponse cancelRecurrence(String recurrenceId) {
        return cancelRecurrence(recurrenceId, null);
    }

    CelcoinPixAutoCancelResponse cancelRecurrence(String recurrenceId, String idempotencyKey);

    List<CelcoinPixAutoRejectionReason> rejectionReasons();
}
