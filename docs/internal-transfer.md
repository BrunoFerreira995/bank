# Transferência entre contas

O SDK implementa transferências entre contas BaaS do mesmo ecossistema Celcoin.
O endpoint utilizado é `POST /baas/v2/wallet/internal/transfer`.

```java
var request = new CelcoinInternalTransferRequest(
    "444444", "10545584", BigDecimal.valueOf(25.55), "repasse", "transfer-1", "11122233344");

var transfer = celcoinClient.accounts().transfer(request, "transfer-1");
var status = celcoinClient.accounts().getTransferStatus(
    transfer.transferId(), transfer.clientRequestId(), transfer.endToEndId());
```

O request contém `amount`, `clientRequestId`, `debitParty.account`,
`creditParty.account`, `creditParty.taxId` opcional e `description`.
O status pode ser consultado por `Id`, `ClientRequestId` ou `EndToEndId`, usando
`PROCESSING`, `CONFIRMED` e `ERROR` como estados principais.
