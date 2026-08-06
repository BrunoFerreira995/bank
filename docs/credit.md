# cel_credit

O módulo `credit` utiliza a autenticação própria da Plataforma de Crédito
Celcoin, separada da autenticação BaaS. O token é obtido por OAuth 2.0
`client_credentials` em `sandbox.auth.flowfinance.com.br` e as operações usam
`sandbox.platform.flowfinance.com.br`.

Operações disponíveis no SDK:

- autenticação da plataforma;
- cadastro e atualização de pessoas, empresas e responsáveis legais;
- criação, consulta e listagem de solicitações;
- simulação de produto (`/banking/originator/products/{id}/preview`);
- variáveis personalizadas;
- consulta e envio de assinaturas da CCB por timestamp ou assinatura física em PDF;
- consulta e qualificação de operações, desaverbação de garantia;
- cadastro, consulta, atualização e exclusão de webhooks do originador;
- consulta de margem e fluxos de consentimento do Crédito do Trabalhador.

Configure `CELCOIN_CREDIT_CLIENT_ID` e `CELCOIN_CREDIT_CLIENT_SECRET` antes de
usar `celcoinClient.credit()`. O token é reutilizado até próximo do vencimento.

Os webhooks de crédito chegam no formato `{ payload, createdAt, type }`. Os
eventos principais são `APPLICATION_STATUS_UPDATED`,
`PERSON_DOCUMENT_STATUS_UPDATED` e `BUSINESS_DOCUMENT_STATUS_UPDATED`.
Operações de consignado e de assinatura dependem do produto e da configuração
do originador na plataforma Celcoin.

### Assinaturas da CCB

Para assinatura coletada digitalmente fora da plataforma, use os campos de
timestamp exigidos pelo contrato:

```java
credit.sendTimestampSignature(applicationId, Map.of(
    "ip_address", "203.0.113.10",
    "person", personId,
    "signed_at", "2026-08-06T12:00:00Z",
    "user_agent", "Mozilla/5.0"));
```

Para assinatura física, envie o PDF assinado:

```java
credit.sendPhysicalSignature(applicationId, pdfBytes, "ccb-assinada.pdf");
```

O PDF é enviado como `multipart/form-data` no endpoint
`/banking/originator/applications/{application_id}/physical-signature`.
