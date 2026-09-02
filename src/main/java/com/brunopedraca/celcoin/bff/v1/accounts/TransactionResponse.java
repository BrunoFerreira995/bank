package com.brunopedraca.celcoin.bff.v1.accounts;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(String transactionId, OffsetDateTime createdAt, BigDecimal amount, String type, String status) {}
