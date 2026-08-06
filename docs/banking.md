# BaaS & Core Banking

O módulo `banking` integra a gestão de contas, relatórios de saldo/extrato e
transferências internas da Celcoin no sandbox
`https://sandbox.openfinance.celcoin.dev`.

Contratos principais: `/baas/v2/account`, `/baas/v2/wallet`,
`/baas-walletreports/v1/wallet/dayBalance` e
`/baas/v2/wallet/internal/transfer`. A criação Core Banking usa
`/baas-onboarding/v1/account/{natural-person|business}/create`.

No sandbox, use `accounts().addSandboxBalance(...)` para semear saldo de teste.
Esse endpoint não deve ser usado em produção: lá o crédito ocorre por cash-in.
Operações de criação, bloqueio judicial e transferência aceitam
`Idempotency-Key` pela interface do SDK.

```java
celcoinClient.accounts().addSandboxBalance(
    new CelcoinSandboxBalanceRequest("300539137798", BigDecimal.TEN, "seed"));
var balance = celcoinClient.accounts().getBalance("300539137798");
```
