package com.brunopedraca.celcoin.escrow;

import java.util.Map;

public interface CelcoinEscrowOperations {
    CelcoinEscrowTokenResponse authenticate();

    Map<String, Object> requestDocumentUpload(Map<String, Object> request);

    Map<String, Object> createPerson(Map<String, Object> request);

    Map<String, Object> createAccount(Map<String, Object> request);

    Map<String, Object> getAccount(String accountId);

    Map<String, Object> getBalance(String accountId);

    Map<String, Object> getStatement(String accountId, Map<String, Object> filters);

    Map<String, Object> createDestination(String accountId, Map<String, Object> request);

    Map<String, Object> listDestinations(String accountId);

    Map<String, Object> updateDestination(String accountId, String destinationId, Map<String, Object> request);

    void deleteDestination(String accountId, String destinationId);

    Map<String, Object> createPosting(Map<String, Object> request);

    Map<String, Object> listPostings(Map<String, Object> filters);

    Map<String, Object> reviewPosting(String postingId, Map<String, Object> request);

    Map<String, Object> cancelPosting(String postingId, String reason);

    Map<String, Object> createWallet(Map<String, Object> request);

    Map<String, Object> listWallets(String accountId);

    Map<String, Object> updateWallet(String walletId, Map<String, Object> request);

    void archiveWallet(String walletId);

    Map<String, Object> createCharge(String walletId, Map<String, Object> request);

    Map<String, Object> listCharges(String walletId, Map<String, Object> filters);

    void deleteCharge(String walletId, String chargeId);

    Map<String, Object> createDepositRetention(String accountId, Map<String, Object> request);

    Map<String, Object> getDepositRetention(String accountId);

    Map<String, Object> createWebhookConfiguration(String accountId, Map<String, Object> request);

    Map<String, Object> listWebhookConfigurations(String accountId);

    void deleteWebhookConfiguration(String accountId, String webhookId);
}
