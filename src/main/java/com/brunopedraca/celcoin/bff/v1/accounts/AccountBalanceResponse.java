package com.brunopedraca.celcoin.bff.v1.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(name = "MobileV1AccountBalance")
public record AccountBalanceResponse(
        String accountId,
        BigDecimal availableBalance,
        BigDecimal blockedBalance,
        @Schema(example = "BRL") String currency) {}
