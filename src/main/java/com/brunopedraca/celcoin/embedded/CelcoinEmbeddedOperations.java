package com.brunopedraca.celcoin.embedded;

import java.util.Map;

/** Facade das APIs avulsas/embedded do cel_banking. */
public interface CelcoinEmbeddedOperations {
    Map<String, Object> celcoinAccountBalance(String account);

    Map<String, Object> ddaRegister(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> ddaDelete(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> ddaRegisterInvoices(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> ddaRegisterWebhook(Map<String, Object> request);

    Map<String, Object> ddaListWebhooks();

    Map<String, Object> billAuthorize(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> billReserve(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> billCapture(String transactionId, Map<String, Object> request, String idempotencyKey);

    Map<String, Object> billReverse(String transactionId, String idempotencyKey);

    Map<String, Object> billStatus(Map<String, Object> filters);

    Map<String, Object> billOccurrences(Map<String, Object> filters);

    Map<String, Object> billInstitutions();

    Map<String, Object> nfseCreateCompany(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> nfseGetCompany(String companyId);

    Map<String, Object> nfseRegister(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> nfseGet(String serviceInvoiceId);

    Map<String, Object> nfseCancel(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> tedTransfer(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> tedStatus(Map<String, Object> filters);

    Map<String, Object> reconciliation(String resource, Map<String, Object> filters);

    Map<String, Object> cashoutPartners();

    Map<String, Object> cashoutServicePoints(Map<String, Object> request);

    Map<String, Object> cashoutDeposit(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> cashoutWithdraw(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> cashoutToken(Map<String, Object> request, String idempotencyKey);

    Map<String, Object> cancelCashoutToken(String tokenId, String idempotencyKey);
}
