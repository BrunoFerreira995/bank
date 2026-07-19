# Webhooks

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
