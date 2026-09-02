package com.brunopedraca.celcoin.bff.v1.accounts;

import java.util.List;

public record StatementResponse(String accountId, List<TransactionResponse> transactions, Integer page, Integer size, Long total, Boolean hasNext) {}
