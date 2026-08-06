# Webhooks

Além do recebimento local, `client.webhooks()` gerencia as configurações e o
histórico remoto de webhooks BaaS na Celcoin:

Para o produto `CEL_BRICKS WEBHOOKS`, use as operações com sufixo `Bricks`.
Elas utilizam o contrato comum com `context` explícito:

```java
client.webhooks().registerBricks(
        new WebhookDtos.CelBricksWebhookSubscriptionRequest(
                "PIX", "pix-payment-out", "https://api.example.com/webhooks/celcoin",
                new WebhookDtos.WebhookAuth("user", "password", "basic")),
        "bricks-webhook-001");

client.webhooks().listBricks("PIX", "pix-payment-out", true);
client.webhooks().resendBricks(
        "PIX",
        new WebhookDtos.WebhookReplayQuery(null, null, null, true, "pix-payment-out",
                null, null, null, null, null, 1, 100, 100),
        null,
        "bricks-replay-001");
```

O contrato comum usa `POST /common/v1/webhook/subscription`,
`GET/PUT/DELETE /common/v1/webhook/subscription/{context}/{entity}` e
`PUT /common/v1/webhook/replay/{context}/{entity}`. O histórico de reenvio é
limitado a sete dias; depois disso, consulte o status da transação.

```java
var subscription = client.webhooks().register(
        new WebhookDtos.WebhookSubscriptionRequest(
                "pix-payment-in", "https://api.example.com/webhooks/celcoin",
                new WebhookDtos.WebhookAuth("user", "password", "basic")),
        "webhook-register-001");

var configured = client.webhooks().listSubscriptions("pix-payment-in", true);
var entities = client.webhooks().listEntities();
var templates = client.webhooks().listTemplates(
        new WebhookDtos.WebhookTemplateQuery(1, 200, 200, "pix-payment-in", "CONFIRMED"));
```

O módulo também oferece `update`, `delete`, `countReplays`, `replayDetails` e
`resend`. O histórico de reenvio da Celcoin é limitado a sete dias; para datas
anteriores, consulte o status da transação.

O endpoint local `/webhooks/celcoin` valida a assinatura configurada, persiste
o payload, deduplica pelo identificador externo e permite reprocessamento em
`/admin/webhooks/{id}/retry`. O payload deve ser tratado como notificação
transacional; o extrato da Celcoin continua sendo a fonte oficial de
conciliação.

Eventos de infração de saldo são aceitos pelo mesmo receptor e podem ser
desserializados em `WebhookDtos.CelcoinInfractionBalanceEvent`. As entidades
oficiais são `pix-med-balance-blocked` e `pix-med-balance-unblocked`.

O endpoint público é:

```text
POST /webhooks/celcoin
```

O payload deve conter um identificador de evento em `id`, `eventId` ou `externalEventId`, e o tipo em `type` ou `eventType`.

Tabela persistida:

```text
celcoin_webhook_event
```

Status:

- `RECEIVED`
- `PROCESSING`
- `PROCESSED`
- `FAILED`

Quando `celcoin.webhook.secret` está configurado, a assinatura é verificada com HMAC-SHA256 usando os headers `X-Celcoin-Timestamp` e `X-Celcoin-Signature`. O timestamp também é validado contra a janela `celcoin.webhook.replay-window`.

Endpoints administrativos:

```text
GET  /admin/webhooks
POST /admin/webhooks/{id}/retry
```
