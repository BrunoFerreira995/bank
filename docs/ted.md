# TED

O SDK expõe o envio e a consulta de TED do BaaS:

```java
var request = new AccountDtos.CelcoinTedTransferRequest(
        "30023646056255", new BigDecimal("0.01"), "110",
        new AccountDtos.CelcoinTedCreditParty(
                "30306294", "000001", "20", "09958359006",
                "Joao Silva Santos", "CC", "F"), null, "");

var transfer = celcoin.accounts().transferTed(request);
var status = celcoin.accounts().getTedTransferStatus(transfer.body().id());
```

O `clientCode` é gerado automaticamente quando não informado. Os eventos
`spb-transfer-in`, `spb-transfer-out` e `spb-reversal-in` podem ser desserializados
com `WebhookDtos.CelcoinTedWebhookEvent`.
