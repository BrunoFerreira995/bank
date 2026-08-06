# Pix Automático

O módulo `pixauto` implementa as jornadas pagadora e recebedora da API ITP da
Celcoin. O consentimento autoriza uma série de pagamentos; cada ciclo possui
seu próprio status e pode ser consultado ou retentado separadamente.

## Fluxo pagador

1. Crie o consentimento com `createConsent`.
2. Redirecione o usuário para `response.authorizationUrl()`.
3. Receba `code` e `state` e chame `processCallback`.
4. Consulte o consentimento com `getConsentStatus`/`listConsents`.
5. Cancele com `cancelConsent` quando solicitado pelo usuário ou pela operação.

Os campos específicos do contrato Celcoin ficam em `metadata` do request de
consentimento: `brandId`, `redirectUrl`, `contractId`, `startDateTime` e
`referenceStartDate` são obrigatórios. `fixedAmount` não deve ser combinado
com `minimumVariableAmount`/`maximumVariableAmount`.

```java
var consent = client.pixAuto().createConsent(
    new CelcoinPixAutoConsentRequest(
        accountId, payerCpf, payerName, null, new BigDecimal("150.00"),
        "MENSAL", null,
        Map.of(
            "brandId", brandId,
            "redirectUrl", "https://app.example/callback",
            "contractId", "assinatura-123",
            "startDateTime", "2026-09-01T00:00:00Z",
            "referenceStartDate", "2026-09-01",
            "fixedAmount", "150.00",
            "isRetryAccepted", true)),
    "consent-123");
```

## Fluxo recebedor

Após `AUTHORISED`, use `schedule` ou `createReceiveSchedule` para cada ciclo.
`retryReceipt` chama a retentativa oficial e `cancelRecurrence` encerra a
recorrência futura. A liquidação é assíncrona: `RCVD`/`ACCP` podem evoluir para
`ACSC`, `RJCT` ou `CANC`; consulte o status ou processe o webhook.

`retryReceipt` exige a nova `date` e o novo `endToEndId` no payload. A API
mantém `originalRecurringPaymentId` para rastreabilidade da tentativa.

`PixAutoStateMachine` expõe as transições de consentimento
(`AWAITING_AUTHORISATION`, `PARTIALLY_ACCEPTED`, `AUTHORISED`, `REVOKED`,
`CONSUMED` e `REJECTED`) e de pagamento (`RCVD`, `ACCP`, `ACPD`, `SCHD`,
`PDNG`, `ACSC`, `RJCT` e `CANC`).

As datas `startDateTime` e `referenceStartDate` devem respeitar D+2. O
`localInstrument` do produto é `AUTO` e não deve ser substituído por `MANU` ou
`DICT`.

## Endpoints implementados

| Operação | Endpoint |
|---|---|
| Consentimento | `POST /baas/v1/open/itp/automatic-payments/payment-initiation` |
| Callback | `POST /baas/v1/open/itp/payment-initiation/callback` |
| Pagamento recorrente | `POST /baas/v1/open/itp/automatic-payments/payment-initiation/{id}/payments` |
| Consulta | `GET /baas/v1/open/itp/automatic-payments/payment-initiation` |
| Cancelamento | `PATCH /baas/v1/open/itp/automatic-payments/v2/payment-initiation/{id}` |
| Retry | `POST /automatic-payments/v2/pix/recurring-payments/{id}/retry` |

Erros definitivos (`CONSENTIMENTO_INVALIDO`, divergência de consentimento,
limite excedido) não devem ser retentados. Saldo insuficiente e falhas de
infraestrutura podem seguir a janela de retry contratada.

O receptor comum `/webhooks/celcoin` valida assinatura, usa
`x-celcoin-event-id` para deduplicação e processa eventos de consentimento e
liquidação de forma assíncrona.
