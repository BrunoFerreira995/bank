package com.brunopedraca.celcoin.banking;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferResponse;
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
