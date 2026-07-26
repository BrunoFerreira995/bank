# Exemplos

## Token

```bash
curl -X POST http://localhost:8080/demo/auth/token
```

## Saldo

```bash
curl http://localhost:8080/demo/accounts/{accountId}/balance
```

## Conta Core Banking

```java
CelcoinCoreAccountRequest request = new CelcoinCoreAccountRequest(
        "12345678901",
        "Maria Silva",
        "PERSON",
        "maria@example.com",
        "+5511999999999",
        address,
        metadata);

CelcoinAccountResponse response =
        celcoinClient.accounts().createCoreAccount(request, "core-account-1");
```

## Listar Contas

```java
CelcoinAccountListRequest request =
        new CelcoinAccountListRequest(null, "PERSON", "ACTIVE", 0, 20);

CelcoinAccountListResponse response = celcoinClient.accounts().listAccounts(request);
```

## Abertura de Conta KYC PF

```java
CelcoinKycPersonAccountRequest request = new CelcoinKycPersonAccountRequest(
        "12345678901",
        "Maria Silva",
        LocalDate.of(1990, 1, 10),
        "maria@example.com",
        "+5511999999999",
        address,
        financialInformation,
        documents,
        metadata);

CelcoinKycOnboardingResponse response =
        celcoinClient.onboarding().createPersonAccount(request, "kyc-pf-1");
```

## Abertura de Conta KYC PJ

```java
CelcoinKycBusinessAccountRequest request = new CelcoinKycBusinessAccountRequest(
        "12345678000190",
        "Empresa Exemplo LTDA",
        "Empresa Exemplo",
        LocalDate.of(2020, 5, 15),
        "contato@example.com",
        "+551133333333",
        address,
        financialInformation,
        owners,
        documents,
        metadata);

CelcoinKycOnboardingResponse response =
        celcoinClient.onboarding().createBusinessAccount(request, "kyc-pj-1");
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

## Pix Cash-in por Agencia e Conta

```java
CelcoinPixCashInAccountRequest request =
        new CelcoinPixCashInAccountRequest("0001", "12345", BigDecimal.TEN, "cash-in", metadata);

CelcoinPixCashInResponse response =
        celcoinClient.pix().createAccountCashIn(request, "cash-in-account-1");
```

## Pix Cash-in por Chave

```java
CelcoinPixCashInKeyRequest request =
        new CelcoinPixCashInKeyRequest("account-1", "RANDOM", null, BigDecimal.TEN, "cash-in", metadata);

CelcoinPixCashInResponse response =
        celcoinClient.pix().createRandomKeyCashIn(request, "cash-in-key-1");
```

## Pix Cash-in por Cobranca Estatica

```java
CelcoinPixCashInStaticChargeRequest request = new CelcoinPixCashInStaticChargeRequest(
        "account-1",
        BigDecimal.TEN,
        "cash-in",
        "12345678901",
        "Maria Silva",
        metadata);

CelcoinPixCashInResponse response =
        celcoinClient.pix().createStaticChargeCashIn(request, "cash-in-static-1");
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
