package com.brunopedraca.celcoin.banking;

import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinAccountStatusResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinBalanceResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinInternalTransferResponse;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementRequest;
import com.brunopedraca.celcoin.banking.AccountDtos.CelcoinStatementResponse;

public interface CelcoinAccountOperations {
    CelcoinAccountStatusResponse getStatus(String accountId);

    CelcoinAccountResponse getPersonAccount(String accountId);

    CelcoinAccountResponse getBusinessAccount(String accountId);

    CelcoinBalanceResponse getBalance(String accountId);

    CelcoinBalanceResponse getDayBalance(String accountId);

    CelcoinStatementResponse getStatement(CelcoinStatementRequest request);

    default CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request) {
        return transfer(request, null);
    }

    CelcoinInternalTransferResponse transfer(CelcoinInternalTransferRequest request, String idempotencyKey);

    CelcoinInternalTransferResponse getTransferStatus(String transferId);
}
