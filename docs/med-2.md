# MED 2.0

O MED 2.0 está disponível em `client.indirectPix()` e utiliza o fluxo
`pix-indirect/v1` de recuperação de valores.

```java
var recovery = client.indirectPix().createFundsRecovery(
        new IndirectPixDtos.CelcoinFundsRecoveryRequest(
                "AUTOMATED", rootEndToEnd, "SCAM", "QR Code fraudulento",
                new IndirectPixDtos.CelcoinFundsRecoveryContact("med@example.com", "+5511999999999"),
                new IndirectPixDtos.CelcoinTrackingGraphParameters(
                        new BigDecimal("200.00"), 5, "PT2H", 10)),
        "med-create-001");

var details = client.indirectPix().getFundsRecovery(fundsRecoveryId);
var graph = client.indirectPix().getFundsRecoveryGraph(fundsRecoveryId);
```

Também estão disponíveis cancelamento e atualização da recuperação, fechamento
da infração (`AGREED`/`DISAGREED`) e criação/fechamento da solicitação de
devolução. Os bloqueios e desbloqueios de saldo são comandados
automaticamente pela Celcoin/DICT; o cliente acompanha os eventos
`pix-med-balance-blocked`, `pix-med-balance-unblocked`, `pix-infraction` e
`pix-med-refund` pelo receptor comum de webhooks.

Os estados da recuperação são `CREATED`, `TRACKED`, `AWAITING_ANALYSIS`,
`ANALYSED`, `REFUNDING`, `COMPLETED` e `CANCELLED`. Após iniciar a devolução,
a recuperação não pode ser cancelada.
