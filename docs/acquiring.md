# Subadquirência e AaaS

O módulo `acquiring()` cobre o ciclo documentado de cobrança avulsa via cartão:
clientes, cartões, cobranças, retry, captura, estorno, cancelamento e relatório
de recebíveis.

O número da conta BaaS é informado no campo `metadata.account` das requisições
que precisam de conta, como criação de cartão e cobrança.

No sandbox, os cartões `4111 1111 1111 1111` simulam aprovação e
`4242 4242 4242 4242` simulam negação; outros números permanecem aguardando
pagamento.

```java
var charge = celcoin.acquiring().createCharge(request, "charge-001");
var retry = celcoin.acquiring().retryCharge(charge.chargeId(), "retry-001");
var report = celcoin.acquiring().requestReceivablesReport(reportRequest);
```

As contas precisam estar abertas, ativas e credenciadas para utilizar o produto.
