# Transferência entre contas

O SDK implementa transferências entre contas BaaS do mesmo ecossistema Celcoin.
O endpoint utilizado é `POST /baas/v2/wallet/internal/transfer`.

O SDK valida conta de origem/destino diferentes, valor positivo, limites de
identificador e descrição antes do envio. A tabela oficial fica disponível em
`client.accounts().internalTransferErrors()`.

Erros de duplicidade (`CBE100`/`CBE101`) não devem gerar uma nova transferência
automaticamente: consulte o status pelo `transferId`, `clientRequestId` ou
`endToEndId`. O fluxo exige contas ativas no mesmo ecossistema Celcoin.

Principais erros: `CBE123` (saldo insuficiente), `CBE124`/`CBE125` (conta
encerrada), `CBE147`/`CBE148` (conta bloqueada), `CBE312` (mesma conta),
`CBE314`/`CBE315` (conta não encontrada) e `CBE666` (bloqueio MED).

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
