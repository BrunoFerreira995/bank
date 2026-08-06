# Pix Inteligente — Sweeping Accounts

O módulo `sweeping()` implementa transferências recorrentes entre contas de
mesma titularidade. O usuário autoriza um consentimento único; depois, a ITP
cria pagamentos dentro dos limites configurados.

## Fluxo

1. Liste instituições com `listBrands()`.
2. Crie o consentimento com `createConsent` e redirecione para
   `authorizationUrl`.
3. Processe `code`, `id_token` e `state` com `processCallback`.
4. Consulte ou cancele o consentimento. A listagem exige o CPF do titular.
5. Execute cada transferência com `createPayment` após `AUTHORISED`.

No consentimento, `sweepingConfiguration` deve conter os limites oficiais,
como `totalAllowedAmount`, `transactionLimit`, `periodicLimits` e
`startDateTime`. O CPF do `loggedUser` deve ser o mesmo dos creditors.

```java
var brands = client.sweeping().listBrands();
var consent = client.sweeping().createConsent(request, "sweep-consent-1");

var payment = client.sweeping().createPayment(paymentRequest, "sweep-payment-1");
```

Operações de criação, cancelamento e pagamento usam `Idempotency-Key`. O
consentimento deve estar `AUTHORISED`; respeite os limites por transação,
diário, semanal, mensal, anual e o teto total. O pagamento também exige
`riskSignals.manual` ou `riskSignals.automatic` e `ibgeTownCode` com sete
dígitos.

## Estados

`CelcoinSweepingStateMachine` expõe as transições oficiais para consentimentos
(`AWAITING_AUTHORISATION`, `AUTHORISED`, `CONSUMED`, `EXPIRED`, `REVOKED` e
`REJECTED`) e pagamentos (`PDNG`, `SCHD`, `ACSP`, `ACSC`, `RJCT` e `CANC`).
Estados terminais não podem retornar a um estado ativo.

O webhook comum em `/webhooks/celcoin` valida assinatura, deduplica e persiste
eventos de Sweeping. O processamento da liquidação é assíncrono; use webhook
ou consulta para acompanhar `ACSC`/`RJCT`.

Endpoints usados:

- `GET /baas/v1/open/itp/participants/brands?type=PAYMENT`
- `POST /baas/v1/open/itp/sweeping-accounts/payment-initiation`
- `POST /baas/v1/open/itp/payment-initiation/callback`
- `PATCH /baas/v1/open/itp/sweeping-accounts/payment-initiation/{id}`
- `GET /open-keys/itp/api/v2/sweeping-accounts/v2/payment-initiation?cpf=...`
- `GET /open-keys/itp/api/v2/sweeping-accounts/v2/payment-initiation/{id}`
- `POST /baas/v1/open/itp/sweeping-accounts/payment-initiation/{id}/payments`

Sweeping Accounts é diferente do Pix Automático: no primeiro, pagador e
recebedor têm a mesma titularidade; no segundo, o recebedor inicia cobranças
de terceiros.
