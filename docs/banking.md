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

## Paginação e extratos

`CelcoinStatementRequest` mantém a forma legada com três argumentos e também
aceita `page` e `size`. Quando informados, eles são enviados como `Page` e
`Limit` para `/baas/v2/wallet/movement`:

```java
var page = celcoinClient.accounts().getStatement(
    new CelcoinStatementRequest("300539137798", from, to, 0, 50));
```

A resposta expõe `page`, `size`, `total` e `hasNext` quando o ambiente retorna
esses metadados. O SDK preserva a lista `transactions` e o mapa `raw` para
campos adicionais ou versões de resposta do contrato.

## Tabela de erros de gestão de contas

| HTTP | Interpretação | Tratamento recomendado |
|---|---|---|
| 400 | payload, documento, status ou filtro inválido | corrigir entrada; não repetir automaticamente |
| 401/403 | credencial, mTLS ou escopo sem autorização | renovar token/verificar habilitação |
| 404 | conta, onboarding ou recurso inexistente | confirmar identificador e estado |
| 409 | operação incompatível com o estado atual ou duplicidade | consultar estado antes de repetir |
| 422 | regra de negócio/KYC não atendida | tratar como rejeição funcional |
| 429 | limite de consumo atingido | respeitar `Retry-After` |
| 5xx/timeout | falha transitória | repetir apenas operações idempotentes e consultar o status |

O SDK converte respostas HTTP em exceções `CelcoinApiException` e subclasses
de domínio; preserve o correlation/request ID nos logs para suporte Celcoin.

```java
celcoinClient.accounts().addSandboxBalance(
    new CelcoinSandboxBalanceRequest("300539137798", BigDecimal.TEN, "seed"));
var balance = celcoinClient.accounts().getBalance("300539137798");
```
