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

```java
// Pagamento Pix genérico com debitParty/creditParty explícitos.
CelcoinPixPaymentResponse response = celcoinClient.pix().cashOut(
        new CelcoinPixPaymentRequest(
                new BigDecimal("25.55"),
                "client-3",
                "MANUAL",
                "IMMEDIATE",
                "HIGH",
                "TRANSFER",
                null,
                null,
                "mensagem",
                new CelcoinPixDebitParty("444444", null, null, null, null),
                new CelcoinPixCreditParty(
                        "30306294", null, "10545584", "10545584", "11122233344", "Celcoin", "CACC"),
                null),
        "pay-1");
```

```bash
# Endpoint demo equivalente (Idempotency-Key opcional)
curl -X POST http://localhost:8080/demo/pix/cash-out \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: pix-1' \
  -d '{"amount":25.55,"clientCode":"client-3","initiationType":"MANUAL","paymentType":"IMMEDIATE",
       "urgency":"HIGH","transactionType":"TRANSFER",
       "debitParty":{"account":"444444"},
       "creditParty":{"bank":"30306294","account":"10545584","branch":"10545584",
                      "taxId":"11122233344","name":"Celcoin","accountType":"CACC"}}'
```

## Pix Cash-out por Agencia e Conta

```java
CelcoinPixCashOutAccountRequest request = new CelcoinPixCashOutAccountRequest(
        "source-account-1",
        "0001",
        "12345",
        "12345678901",
        "Maria Silva",
        BigDecimal.TEN,
        "cash-out",
        metadata);

CelcoinPixPaymentResponse response =
        celcoinClient.pix().cashOutToAccount(request, "cash-out-account-1");
```

## Pix Cash-out por Chave

```java
CelcoinPixCashOutKeyRequest request = new CelcoinPixCashOutKeyRequest(
        "source-account-1",
        "77517432125",
        "30306294",
        "Maria Silva",
        BigDecimal.TEN,
        "client-1",
        "mensagem");

CelcoinPixPaymentResponse response = celcoinClient.pix().cashOutByKey(request, "cash-out-key-1");
```

## Pix Cash-out por QR Code

```java
// Estático: o valor pode ser informado ou lido do EMV decodificado.
CelcoinPixCashOutStaticQrCodeRequest request = new CelcoinPixCashOutStaticQrCodeRequest(
        "account-1", "000201010212", BigDecimal.TEN, "cash-out");

CelcoinPixPaymentResponse response =
        celcoinClient.pix().cashOutStaticQrCode(request, "cash-out-qr-1");

// Dinâmico: valor vem do próprio QR Code.
CelcoinPixCashOutDynamicQrCodeRequest dynamic =
        new CelcoinPixCashOutDynamicQrCodeRequest("account-1", "000201010212", "cash-out");

CelcoinPixPaymentResponse dynamicResponse =
        celcoinClient.pix().cashOutDynamicQrCode(dynamic, "cash-out-qr-2");
```

## Decodificação de EMV

```java
CelcoinPixEmvDecodeResponse decoded = celcoinClient.pix().decodeEmv("00020101021226360014br.gov.bcb.pix");
// decoded.type() -> STATIC | IMMEDIATE | DUEDATE
// decoded.key()  -> chave Pix do recebedor
```

## QR Code Pix (Cobrança Dinâmica)

```java
CelcoinPixQrCodeRequest request = new CelcoinPixQrCodeRequest(
        "testepix@celcoin.com.br",
        "10.00",
        new CelcoinPixMerchant("5651", "06519435", "barueri", "Teste Celcoin"),
        30000,
        "client-1",
        null,
        null,
        null,
        null);

CelcoinPixQrCodeResponse response = celcoinClient.pix().createQrCode(request, "qr-1");
// response.emv() e response.location() expõem o BRCode
```

```bash
curl -X POST http://localhost:8080/demo/pix/qr-code \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: qr-1' \
  -d '{"key":"testepix@celcoin.com.br","amount":"10.00",
       "merchant":{"merchantCategoryCode":"5651","postalCode":"06519435","city":"barueri","name":"Teste Celcoin"},
       "expiration":30000,"clientRequestId":"client-1"}'
```

## Pix Cash-in por Cobrança Estática

```java
CelcoinPixCashInResponse response = celcoinClient.pix().createStaticChargeCashIn(
        new CelcoinPixStaticChargeRequest(
                "testepix@celcoin.com.br",
                new BigDecimal("10.00"),
                new CelcoinPixMerchant("5651", "06519435", "barueri", "Teste"),
                "txid-1"),
        "cash-in-static-1");
```

## Pix Cash-in por QR Code Duedate

```java
CelcoinPixCashInResponse response = celcoinClient.pix().createDueDateQrCodeCashIn(
        new CelcoinPixDueDateQrCodeRequest(
                "testepix@celcoin.com.br",
                "client-2",
                "15.63",
                10,
                OffsetDateTime.now().plusDays(1),
                null,
                null,
                null,
                null,
                null,
                null),
        "cash-in-due-1");
```

## Consultar Recebimentos e Movimentações

```java
// Status de um recebimento específico (por endToEndId, transactionId ou brcode).
CelcoinPixReceiptResponse receipt =
        celcoinClient.pix().getCashInReceipt(
                new CelcoinPixReceiptRequest("E1393589320", null, null, null));

// Extrato de movimentações (PIXPAYMENTIN/PIXPAYMENTOUT) de uma conta.
CelcoinPixMovementResponse movements = celcoinClient.pix().getMovements(
        new CelcoinPixMovementRequest(
                "30054065526",
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                50,
                null,
                "desc"));
```

## Devolução de Pix

```java
CelcoinPixRefundResponse refund = celcoinClient.pix().refund(
        new CelcoinPixRefundRequest(
                "pay-1", "E1393589320", "client-1", new BigDecimal("15.00"), "BE08", null),
        "refund-1");

CelcoinPixRefundResponse status = celcoinClient.pix().getRefund(refund.returnIdentification());

CelcoinPixDevolutionStatusResponse devolution =
        celcoinClient.pix().getDevolution(refund.returnIdentification());
```

## Gerenciamento de Chaves (DICT)

```java
// Cadastrar chave
CelcoinPixKeyResponse created = celcoinClient.pix().createKey(
        new CelcoinPixKeyRequest("30054065526", "EMAIL", "teste@celcoin.com.br"), "key-1");

// Listar chaves de uma conta
CelcoinPixKeyListResponse keys = celcoinClient.pix().listKeys("30054065526");

// Alterar nome da chave
CelcoinPixKeyUpdateResponse renamed = celcoinClient.pix().updateKeyName(
        new CelcoinPixUpdateKeyRequest(
                "30054065526", "teste@celcoin.com.br", "Lavanderia 2 irmaos ltda", "Lavanderia 2 irmaos"),
        "key-upd-1");

// Excluir chave
CelcoinPixKeyOperationResponse deleted = celcoinClient.pix().deleteKey(
        new CelcoinPixDeleteKeyRequest("30054065526", "teste@celcoin.com.br"), "key-del-1");

// Consultar chave de outra instituição (DICT externo)
CelcoinPixKeyLookupResponse lookup = celcoinClient.pix().lookupKey("30054065526", "77517432125");
```

## Split de Pix Cash-in

```java
CelcoinPixSplitFeeInfo fee = new CelcoinPixSplitFeeInfo(
        10,
        new BigDecimal("10.00"),
        List.of(new CelcoinPixSplitFeeDetail(
                new BigDecimal("8.00"), "descricao", "cc-1", "30054065526")));

// Split imediato
CelcoinPixSplitResponse immediate = celcoinClient.pix().createImmediateSplitQrCode(
        new CelcoinPixImmediateSplitRequest(
                "split-1", null, "testepix@celcoin.com.br", null, null, null, null, null, fee),
        "split-1");

// Split duedate
CelcoinPixSplitResponse due = celcoinClient.pix().createDueDateSplitQrCode(
        new CelcoinPixDueDateSplitRequest(
                "split-2",
                10,
                OffsetDateTime.now().plusDays(1),
                null,
                null,
                null,
                new BigDecimal("18.00"),
                null,
                null,
                null,
                null,
                "testepix@celcoin.com.br",
                fee),
        "split-2");
```

## Agendamento de Pix

```java
CelcoinPixScheduleResponse scheduled = celcoinClient.pix().schedule(
        new CelcoinPixScheduleRequest(
                new BigDecimal("0.01"),
                "client-1",
                "30054065526",
                new CelcoinPixCreditParty("08561701", null, "305157919", "1", "06237998128", "Julio", "CACC"),
                "2026-08-10",
                "mensagem",
                null),
        "sched-1");

CelcoinPixScheduleResponse detail = celcoinClient.pix().getSchedule(scheduled.schedulerId());
CelcoinPixScheduleResponse cancelled = celcoinClient.pix().cancelSchedule(scheduled.schedulerId(), "cancel-1");

CelcoinPixScheduleListResponse list = celcoinClient.pix().listSchedules(
        new CelcoinPixScheduleListRequest(
                "30054065526", LocalDate.now(), LocalDate.now().plusDays(30), 1, 20, "SCHEDULED"));
```

## Portabilidade e Reivindicação de Chaves

```java
CelcoinPixClaimResponse claim = celcoinClient.pix().claimKey(
        new CelcoinPixClaimRequest("teste@celcoin.com.br", "EMAIL", "30054065526", "PORTABILITY"),
        "claim-1");

CelcoinPixClaimResponse confirmed = celcoinClient.pix().confirmClaim(claim.id(), "USER_REQUESTED", "confirm-1");
CelcoinPixClaimResponse cancelled = celcoinClient.pix().cancelClaim(claim.id(), "FRAUD", "cancel-1");

CelcoinPixClaimResponse byId = celcoinClient.pix().getClaim(claim.id());

CelcoinPixClaimListResponse claims = celcoinClient.pix().listClaims(
        new CelcoinPixClaimListRequest(
                LocalDate.now().minusDays(7), LocalDate.now(), null, 1, "OPEN", "PORTABILITY"));
```

## Pix Automatico

```java
CelcoinPixAutoConsentRequest consentRequest = new CelcoinPixAutoConsentRequest(
        "account-1",
        "12345678901",
        "Maria Silva",
        "12345678",
        new BigDecimal("1000.00"),
        "MONTHLY",
        10,
        metadata);

CelcoinPixAutoConsentResponse consent =
        celcoinClient.pixAuto().createConsent(consentRequest, "pix-auto-consent-1");

CelcoinPixAutoScheduleRequest scheduleRequest = new CelcoinPixAutoScheduleRequest(
        "account-1",
        consent.consentId(),
        new BigDecimal("250.00"),
        LocalDate.now().plusDays(7),
        "MONTHLY",
        10,
        metadata);

CelcoinPixAutoScheduleResponse schedule =
        celcoinClient.pixAuto().schedule(scheduleRequest, "pix-auto-schedule-1");

CelcoinPixAutoScheduleStatusResponse status =
        celcoinClient.pixAuto().getScheduleStatus(schedule.scheduleId());
```

## Boleto

```bash
curl -X POST http://localhost:8080/demo/boletos \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: boleto-1' \
  -d '{"accountId":"acc","amount":50.00,"dueDate":"2026-08-10","payer":{}}'
```

## Subadquirencia e AaaS

```java
CelcoinAcquiringCustomerRequest customer = new CelcoinAcquiringCustomerRequest(
        null,
        "12345678901",
        "Maria Silva",
        "maria@example.com",
        "+5511999999999",
        address,
        metadata);

CelcoinAcquiringCustomerResponse response =
        celcoinClient.acquiring().createCustomer(customer, "acquiring-customer-1");
```

## Cartoes

```java
CelcoinCardAccountRequest account = new CelcoinCardAccountRequest(
        "12345678901",
        "Maria Silva",
        "maria@example.com",
        "+5511999999999",
        address,
        metadata);

CelcoinCardAccountResponse accountResponse =
        celcoinClient.cards().createCardAccount(account, "card-account-1");

CelcoinCardIssueRequest card = new CelcoinCardIssueRequest(
        accountResponse.cardAccountId(),
        "Maria Silva",
        "PHYSICAL",
        deliveryAddress,
        metadata);

CelcoinCardResponse cardResponse =
        celcoinClient.cards().issueCard(card, "issue-card-1");
```

## Webhook

```bash
curl -X POST http://localhost:8080/webhooks/celcoin \
  -H 'Content-Type: application/json' \
  -d '{"id":"evt-1","type":"pix.cashin"}'
```
