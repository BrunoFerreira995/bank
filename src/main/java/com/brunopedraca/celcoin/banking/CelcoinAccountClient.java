package com.brunopedraca.celcoin.banking;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountCountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountCustomerUpdateRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountClosureRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountFinancialInformationRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountListRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountListResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountMonitoringRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountMonitoringResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinCoreAccountRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinJudicialBlockRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinJudicialBlockResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinSandboxBalanceRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;

public class CelcoinAccountClient implements CelcoinAccountOperations {
    @SuppressWarnings("unused")
    private final CelcoinHttpClient httpClient;

    public CelcoinAccountClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CelcoinAccountStatusResponse getStatus(String accountId) {
        throw unspecified();
    }

    public CelcoinAccountResponse getPersonAccount(String accountId) {
        throw unspecified();
    }

    public CelcoinAccountResponse getBusinessAccount(String accountId) {
        throw unspecified();
    }

    public CelcoinAccountResponse createCoreAccount(CelcoinCoreAccountRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAccountResponse updateFinancialInformation(CelcoinAccountFinancialInformationRequest request) {
        throw unspecified();
    }

    public CelcoinAccountResponse updateCustomer(CelcoinAccountCustomerUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinAccountStatusResponse deactivateAccount(CelcoinAccountClosureRequest request) {
        throw unspecified();
    }

    public CelcoinAccountStatusResponse closeAccount(CelcoinAccountClosureRequest request) {
        throw unspecified();
    }

    public CelcoinAccountListResponse listAccounts(CelcoinAccountListRequest request) {
        throw unspecified();
    }

    public CelcoinAccountCountResponse countAccounts(CelcoinAccountListRequest request) {
        throw unspecified();
    }

    public CelcoinJudicialBlockResponse createJudicialBlock(CelcoinJudicialBlockRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinAccountStatusResponse updateStatus(CelcoinAccountStatusUpdateRequest request) {
        throw unspecified();
    }

    public CelcoinBalanceResponse addSandboxBalance(CelcoinSandboxBalanceRequest request) {
        throw unspecified();
    }

    public CelcoinAccountMonitoringResponse createMonitoring(CelcoinAccountMonitoringRequest request) {
        throw unspecified();
    }

    public CelcoinAccountMonitoringResponse simulateMonitoring(String monitoringId, String status) {
        throw unspecified();
    }

    public CelcoinBalanceResponse getBalance(String accountId) {
        throw unspecified();
    }

    public CelcoinBalanceResponse getDayBalance(String accountId) {
        throw unspecified();
    }

    public CelcoinStatementResponse getStatement(CelcoinStatementRequest request) {
        throw unspecified();
    }

    public CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request, String idempotencyKey) {
        throw unspecified();
    }

    public CelcoinInternalTransferResponse getTransferStatus(String transferId) {
        throw unspecified();
    }

    private CelcoinIntegrationException unspecified() {
        return new CelcoinIntegrationException(
                "Celcoin account endpoint path is not configured because the official contract was not provided in this first version");
    }
}
