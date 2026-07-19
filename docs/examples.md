# Exemplos

## Token

```bash
curl -X POST http://localhost:8080/demo/auth/token
```

## Saldo

```bash
curl http://localhost:8080/demo/accounts/{accountId}/balance
```

## Pix Cash-out

```bash
curl -X POST http://localhost:8080/demo/pix/cash-out \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: pix-1' \
  -d '{"accountId":"acc","pixKey":"chave","amount":10.00,"description":"teste"}'
```

## QR Code Pix

```bash
curl -X POST http://localhost:8080/demo/pix/qr-code \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: qr-1' \
  -d '{"amount":10.00,"description":"teste","metadata":{}}'
```

## Boleto

```bash
curl -X POST http://localhost:8080/demo/boletos \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: boleto-1' \
  -d '{"accountId":"acc","amount":50.00,"dueDate":"2026-08-10","payer":{}}'
```

## Webhook

```bash
curl -X POST http://localhost:8080/webhooks/celcoin \
  -H 'Content-Type: application/json' \
  -d '{"id":"evt-1","type":"pix.cashin"}'
```
