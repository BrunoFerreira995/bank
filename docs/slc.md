# SLC (domicílio de arranjo de cartões)

O SLC é recebido de forma assíncrona: a Celcoin credita a conta escolhida como
domicílio bancário e envia o evento `slc-payment-in` pelo Webhook Manager. Não
há operação de iniciação ou consulta de liquidação no contrato público.

O SDK expõe um parser tipado para o payload recebido:

```java
var event = client.slc().parsePaymentIn(payloadJson);
var amount = event.body().amount();
var arrangement = event.body().tags().stream()
        .filter(tag -> "PaymentArrangement".equals(tag.key()))
        .findFirst()
        .map(SlcDtos.Tag::value)
        .orElse(null);
```

Eventos suportados:

- `slc-payment-in`: crédito de recebíveis, com bandeira, adquirente e dados do ponto de venda em `body.tags`.
- `spb-event-error`: erro operacional de liquidação, normalmente por saldo insuficiente; consulte `parseSettlementError`.

Antes de receber os créditos, o produto precisa ser habilitado pela implantação
Celcoin e o webhook `slc-payment-in` deve ser cadastrado no Webhook Manager.
Os lançamentos podem ser conciliados pelo extrato da conta.
