# Pagamento de boletos

O fluxo de pagamento possui três etapas:

1. `boletos().authorize(...)` consulta e valida o código de barras ou a linha
   digitável em `POST /v5/transactions/billpayments/authorize`;
2. `boletos().pay(...)` efetiva o pagamento em `POST /baas/v2/billpayment`;
3. `boletos().getPaymentStatus(...)` consulta o resultado em
   `GET /baas/v2/billpayment/status`.

O `transactionId` retornado na autorização deve ser enviado como
`transactionIdAuthorize` na efetivação. O `account` é a conta BaaS debitada e
`clientRequestId` deve ser único por pagamento.

```java
var authorized = celcoinClient.boletos().authorize(
    new CelcoinBoletoAuthorizationRequest(
        "app", 1L,
        new CelcoinBoletoBarCode(2, "23793381286008301352856000063307789840000150000", null)));

var payment = celcoinClient.boletos().pay(new CelcoinBoletoPaymentRequest(
    "boleto-1", authorized.totalUpdated(), "300539137798",
    authorized.transactionId(), null,
    new CelcoinBoletoBarCode(2, "23793381286008301352856000063307789840000150000", null)),
    "boleto-1");
```

No sandbox, utilize a massa de boletos oficial da Celcoin. Códigos arbitrários
são rejeitados pelo ambiente de testes.

O SDK valida previamente os campos obrigatórios da efetivação e expõe a tabela
de erros por `client.boletos().paymentErrors()`. Os códigos `PCE025` e `PCE026`
indicam possível duplicidade: consulte `getPaymentStatus` antes de reenviar.
Em falhas transitórias como `PCE092`, consulte o status antes de tentar
novamente.

Principais erros: `PCE009` (protocolo de autorização ausente), `PCE010`
(conta não encontrada), `PCE011` (identificador maior que 20 caracteres),
`PCE012`/`PCE014` (valor inválido), `PCE040` (valor acima do máximo) e
`PCE050` (boleto não permite alterar valor). A tabela completa está disponível
no catálogo do SDK e na [documentação oficial da Celcoin](https://developers.celcoin.com.br/docs/tabela-de-erros-pagamento-de-contas).
