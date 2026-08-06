package com.brunopedraca.celcoin.credit;

import java.time.LocalDate;

public record CelcoinCreditApplicationQuery(
        Integer page,
        Integer size,
        String borrowerId,
        String productId,
        String status,
        String externalId,
        String taxpayerId,
        LocalDate createdDateFrom,
        LocalDate createdDateTo,
        Long sequentialId) {}
