# Consignado Crédito Trabalhador

O SDK cobre os dois pontos de entrada do Crédito do Trabalhador: a jornada
sem leilão, conduzida pelo originador, e a jornada com leilão interno.

## Oferta no leilão

```java
var offer = Map.of(
    "installment_quantity", 10,
    "installment_amount", 200,
    "available_balance", 1200,
    "amount", 1500,
    "iof", 0,
    "annual_tax", 1.2,
    "cet", 1.1,
    "interest_tax", 0.1,
    "monthly_cet", 0.1,
    "insurance_amount", 12,
    "entry_url", "https://originador.example/credito");

var result = celcoinClient.credit()
    .submitWorkersCreditOffer("proposal-id", offer);
```

A devolutiva `WorkersCreditAuctionResult` chega pelo webhook com status
`APPROVED`, `DENIED` ou `ERROR`. Uma oferta aprovada ainda passa por KYC,
assinatura da CCB, averbação e envio do contrato.

## FGTS e garantia

```java
var balance = celcoinClient.credit()
    .getFgtsBalance("product-id", "12345678901");
var guarantee = celcoinClient.credit()
    .getGuaranteeStatus("application-id");
```

O resultado de margem pode ser síncrono ou assíncrono (`GuaranteeBalanceUpdate`)
e depende de autorização do trabalhador no aplicativo oficial do FGTS.

Eventos operacionais de escrituração, repasse e alteração de vínculo podem ser
consultados com `listGuaranteeEvents`, usando `event_type` `BOOKKEEPING`,
`TRANSFER` ou `ALTER_ANNOTATION`.

## Averbação Dataprev

Falhas transitórias são reprocessadas automaticamente pela Celcoin. Falhas
definitivas cancelam a operação e aparecem no status/webhook com o código
retornado pela Dataprev. O SDK não tenta averbar novamente por conta própria.
