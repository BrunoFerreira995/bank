# Subadquirência e AaaS

O módulo `acquiring()` cobre o ciclo documentado de cobrança avulsa via cartão:
clientes, cartões, cobranças, retry, captura, estorno, cancelamento e relatório
de recebíveis.

Também estão disponíveis planos e assinaturas recorrentes, transações de
assinaturas, chargebacks, webhooks de chargeback, taxas, transações e extrato
de recebíveis. Essas operações usam os endpoints `baas/v1/cash/plans`,
`subscriptions`, `transactions`, `chargebacks`, `webhooks`, `company/fees` e
`receivables/statement`.

O número da conta BaaS é informado no campo `metadata.account` das requisições
que precisam de conta, como criação de cartão e cobrança.

No sandbox, os cartões `4111 1111 1111 1111` simulam aprovação e
`4242 4242 4242 4242` simulam negação; outros números permanecem aguardando
pagamento.

```java
var charge = celcoin.acquiring().createCharge(request, "charge-001");
var retry = celcoin.acquiring().retryCharge(charge.chargeId(), "retry-001");
var report = celcoin.acquiring().requestReceivablesReport(reportRequest);

var plan = celcoin.acquiring().createPlan(planRequest, "plan-001");
var subscriptions = celcoin.acquiring().listSubscriptions(listRequest);
var chargebacks = celcoin.acquiring().listChargebacks(listRequest);
```

Tokenização exige o número da conta explicitamente:

```java
var token = celcoin.acquiring().tokenizeCard(accountId, tokenRequest);
```

As contas precisam estar abertas, ativas e credenciadas para utilizar o produto.
