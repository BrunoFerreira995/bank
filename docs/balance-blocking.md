# Bloqueio e desbloqueio de saldo

O módulo de contas expõe bloqueios correlacionáveis e desbloqueios totais ou
parciais:

```java
var block = client.accounts().blockBalance(
        new AccountDtos.CelcoinBalanceBlockRequest(
                account, new BigDecimal("100.00"), "block-001", "correlation-001",
                "GARANTIA", "Reserva operacional", List.of()), "block-001");

var unblock = client.accounts().unblockBalance(
        new AccountDtos.CelcoinBalanceUnblockRequest(
                account, "unblock-001", "correlation-001", "LIBERACAO",
                "Liberação parcial", false, new BigDecimal("25.00")), "unblock-001");
```

O bloqueio exige valor mínimo de R$ 0,01, `clientRequestId`,
`correlationBlockedId`, motivo e descrição. O desbloqueio total usa
`unBlockAll=true`; o parcial exige `amount` mínimo de R$ 0,01. Os eventos de
confirmação chegam como `balance-amount-event` e são persistidos pelo receptor
comum de webhooks.
