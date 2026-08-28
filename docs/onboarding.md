# Onboarding KYC - Implantação

O SDK implementa o produto **Onboarding KYC** da Celcoin (criação de contas no
BaaS) contra o ambiente de sandbox (`https://sandbox.openfinance.celcoin.dev`).
Esta página cobre o fluxo de abertura de conta PF/PJ, a consulta de propostas, a
atualização cadastral e os webhooks de acompanhamento.

## Fluxo funcional

1. **Criar proposta** — para Pessoa Física use `createPersonAccount` e para
   Pessoa Jurídica use `createBusinessAccount`. A resposta retorna o
   `proposalId`, que deve ser persistido para consulta de status e
   acompanhamento via webhook.
2. **Envio de documentos** — não há endpoint dedicado de upload: os documentos
   devem estar acessíveis via URL pública e são enviados dentro da própria
   proposta, no campo `files[]` (`type` + `data`). A URL precisa ser acessível
   pela Celcoin e recomenda-se validade mínima de 1 hora.
3. **Background check e documentoscopia** — após a criação, a Celcoin roda a
   política de Background Check e a Documentoscopia. Os resultados são
   entregues via webhook (`onboarding-backgroundcheck`,
   `onboarding-documentscopy`, `onboarding-file`).
4. **Resultado da proposta** — o webhook `onboarding-proposal` notifica
   `APPROVED` ou `REPROVED`. Também é possível consultar o status
   sincronamente com `getProposal`.
5. **Criação da conta BaaS** — após aprovação, o webhook `onboarding-create`
   notifica a criação da conta (`CONFIRMED`) ou erro (`ERROR`).

> A conta BaaS precisa existir no sandbox para os fluxos de DICT, cash-out e
> extrato. Se ela não existir, chamadas como
> `GET /baas/v2/pix/dict/entry/external/{account}` retornam `CBE030`.

## Capacidades implementadas

| Área | Métodos (`CelcoinOnboardingOperations`) | Endpoint Celcoin |
| --- | --- | --- |
| Proposta Pessoa Física | `createPersonAccount` | `POST /onboarding/v1/onboarding-proposal/natural-person` |
| Proposta Pessoa Jurídica | `createBusinessAccount` | `POST /onboarding/v1/onboarding-proposal/legal-person` |
| Consulta de proposta | `getProposal` | `GET /onboarding/v1/onboarding-proposal?ProposalId=` |
| Atualização cadastral PF | `updatePersonAccount` | `PUT /onboarding/v1/onboarding-proposal/account/{account}/natural-person` |
| Cadastro de webhook | `createWebhookSubscription` | `POST /baas/v2/webhook/subscription` |

## Biometria, prova de vida e erros

`SELFIE` pode ser enviado em `files[]` para os fluxos de análise documental.
Autenticação biométrica e prova de vida interativa, entretanto, são produtos
ou jornadas específicas da Celcoin; o contrato consultado não expõe endpoint
REST dedicado para o SDK. A aplicação deve habilitar a jornada Celcoin/WebView
correspondente e tratar o resultado por webhook/status, sem simular aprovação
localmente.

| HTTP/código | Cenário KYC | Ação |
|---|---|---|
| 400 | payload, data ou documento inválido | corrigir a proposta |
| 401/403 | token, mTLS ou escopo inválido | renovar credencial/verificar habilitação |
| 404 | proposta inexistente | confirmar `proposalId` |
| 409 | proposta duplicada ou estado incompatível | consultar a proposta antes de repetir |
| 422 | regra de KYC, documento ou dado obrigatório não atendido | solicitar correção cadastral |
| 429 | limite de requisições | obedecer `Retry-After` |
| 5xx/timeout | falha transitória | repetir com idempotência e consultar status |

Os códigos funcionais retornados no corpo (`errorCode`/`message`) devem ser
preservados junto do `proposalId`, correlation ID e request ID para suporte.

## Proposta Pessoa Física (PF)

`POST /onboarding/v1/onboarding-proposal/natural-person`

Campos obrigatórios: `clientCode`, `documentNumber` (CPF), `phoneNumber`,
`email`, `motherName`, `fullName`, `birthDate` (formato `DD-MM-AAAA`),
`address`. `onboardingType` assume `"BAAS"` por padrão.

```json
{
  "clientCode": "a7e9ea3f-69e4-4599-92b4-6cb8a79c3512",
  "documentNumber": "91170215025",
  "phoneNumber": "+5511912345678",
  "email": "testekyc@celcoin.com.br",
  "motherName": "Teste Mae",
  "fullName": "Teste teste",
  "socialName": "",
  "birthDate": "31-12-2000",
  "address": {
    "postalCode": "06455030",
    "street": "Alameda Xingu",
    "number": "350",
    "addressComplement": "",
    "neighborhood": "Alphaville Industrial",
    "city": "Barueri",
    "state": "SP"
  },
  "isPoliticallyExposedPerson": false,
  "onboardingType": "BAAS",
  "financialDetails": {
    "declaredIncome": "1DINP02",
    "occupation": "ONP07",
    "netWorth": "NWNP02"
  },
  "files": [
    { "type": "SELFIE", "data": "https://cloud.storage/selfie.jpeg" }
  ]
}
```

Tipos de arquivo aceitos em `files[].type`: `SELFIE`, `CNH_FRONT`, `CNH_BACK`,
`RG_FRONT`, `RG_BACK`, `RNE_FRONT`, `RNE_BACK`, `RNM_FRONT`, `RNM_BACK`,
`PASSPORT_FRONT`, `PASSPORT_BACK`, `CTPS_FRONT`, `CTPS_BACK`, `CRLV_FRONT`,
`CRLV_BACK`, `CONTRATO_SOCIAL`, `COMPROVANTE_RESIDENCIA`, `COMPROVANTE_RENDA`,
`DOCUMENTO_FINANCEIRO`, `PROCURACAO_PODERES`, `DOCUMENTO_LEGAL`,
`DECLARACAO_ANUAL`, `DECLARACAO_CONFIRMACAO`, `DRE`, `OUTROS`.

### Resposta

```json
{
  "version": "1.0.0",
  "status": "PROCESSING",
  "body": {
    "proposalId": "de20636c-5361-4df4-8a34-c01995a6976d",
    "clientCode": "b9a77b3d-b519-4193-ac59-4f88de04d8a4",
    "documentNumber": "83262483559"
  }
}
```

## Proposta Pessoa Jurídica (PJ)

`POST /onboarding/v1/onboarding-proposal/legal-person`

Campos obrigatórios: `clientCode`, `contactNumber`, `documentNumber` (CNPJ),
`businessEmail`, `businessName` (razão social), `tradingName` (nome fantasia),
`owner` (quadro societário) e `businessAddress`. `companyType` assume `"PJ"`
por padrão (`PJ`, `MEI` ou `ME`).

Regras do quadro societário:

- O primeiro item do array `owner` deve ser **sempre uma Pessoa Física**
  (`ownerType = "SOCIO"` ou `"REPRESENTANTE"`), responsável pelo envio dos
  documentos.
- Sócios Pessoa Física adicionais usam `ownerType = "DEMAIS_SOCIOS"`.
- Caso todos os sócios sejam Pessoa Jurídica, é obrigatório informar um
  representante legal (`ownerType = "REPRESENTANTE"`) com
  `PROCURACAO_PODERES` no primeiro registro.
- Informe pelo menos os sócios com participação societária maior ou igual a
  25%.

```json
{
  "clientCode": "a7e9ea3f-69e4-4599-92b4-6cb8a79c3512",
  "contactNumber": "+5511912345678",
  "documentNumber": "87649940000194",
  "businessEmail": "testekyc@celcoin.com.br",
  "businessName": "Celcoin",
  "tradingName": "Celcoin Instituicao de Pagamento",
  "companyType": "PJ",
  "owner": [
    {
      "ownerType": "SOCIO",
      "documentNumber": "72352781027",
      "fullName": "Nome Teste",
      "phoneNumber": "+5511912345128",
      "email": "sociokyc@celcoin.com.br",
      "motherName": "Nome Mae",
      "socialName": "Nome",
      "birthDate": "02-02-1990",
      "address": {
        "postalCode": "06455030",
        "street": "Alameda Xingu",
        "number": "50",
        "neighborhood": "Alphaville Industrial",
        "city": "Barueri",
        "state": "SP"
      },
      "isPoliticallyExposedPerson": false,
      "financialOwnerDetails": {
        "ownerDeclaredIncome": "ODIB02"
      }
    }
  ],
  "businessAddress": {
    "postalCode": "06455030",
    "street": "Alameda Xingu",
    "number": "350",
    "neighborhood": "Alphaville Industrial",
    "city": "Barueri",
    "state": "SP"
  },
  "onboardingType": "BAAS",
  "financialCompanyDetails": {
    "declaredCompanyRevenue": "DCRB03"
  }
}
```

## Consulta de proposta

`GET /onboarding/v1/onboarding-proposal?ProposalId={proposalId}`

Status possíveis: `CREATED`, `PENDING`, `PENDING_DOCUMENTSCOPY`, `APPROVED`,
`REPROVED`, `RESOURCE_ERROR`, `RESOURCE_CREATED`, `PROCESSING_DOCUMENTSCOPY`.

## Atualização cadastral PF

`PUT /onboarding/v1/onboarding-proposal/account/{account}/natural-person`

Cria uma proposta de atualização dos dados cadastrais (nome, e-mail, telefone,
endereço) de uma Pessoa Física existente. A resposta retorna o
`updateProposalId`.

## Webhooks de onboarding

Os eventos são recebidos na URL configurada via `createWebhookSubscription`
(entity + URL + autenticação). Eventos disponíveis:

| Entity | Descrição |
| --- | --- |
| `onboarding-backgroundcheck` | Status do processo de Background Check |
| `onboarding-documentscopy` | Status do processo de Documentoscopia |
| `onboarding-file` | URL contendo os documentos enviados na jornada (após documentoscopia com status `Processing`) |
| `onboarding-proposal` | Resultado da proposta: `APPROVED` ou `REPROVED` |
| `onboarding-create` | Criação da conta no BaaS: `CONFIRMED` ou `ERROR` |

Modelo do webhook `onboarding-proposal`:

```json
{
  "webhookId": "412efd4d-5c8f-47fb-b837-71211956b1ef",
  "createTimestamp": "2024-03-06T13:19:40Z",
  "entity": "onboarding-proposal",
  "status": "APPROVED",
  "body": {
    "proposalId": "0bd5-22d3-4454-8bd7-9...",
    "clientCode": "d12304dd-8b1...",
    "documentNumber": "48087438000185",
    "proposalType": "PJ",
    "onboardingType": "BAAS"
  }
}
```

## Observações

- **Autenticação biométrica e prova de vida** são operações do produto separado
  de Autenticação Biométrica. O SDK expõe criação, consulta, documentos e o
  webhook `onboarding-biometric-auth`; a contratação do produto continua sendo
  necessária.
- **Idempotência**: `createPersonAccount` e `createBusinessAccount` aceitam
  `idempotencyKey`, aplicada via cabeçalho `Idempotency-Key`.

## Contrato de início do cadastro no BFF mobile

A jornada mobile inicia o cadastro pelo BFF em
`POST /mobile/v1/onboardings`. O retorno mínimo obrigatório para a aplicação é
JSON com o identificador do onboarding e o estado inicial:

```json
{
  "onboardingId": "onb-e2e-123",
  "status": "PENDING"
}
```

`onboardingId` é necessário para acompanhar a jornada e correlacionar os
consentimentos, webhooks e evidências de teste. Se o BFF responder com corpo
vazio, `204`, ou sem esse campo, o app não deve exibir um identificador
indefinido: deve informar que o cadastro foi recebido sem identificador e a
integração deve ser corrigida antes de considerar o fluxo concluído.

Para o E2E iOS, a massa precisa existir no ambiente apontado por `BFF_BASE_URL`
e o endpoint deve devolver esse contrato. O SDK de onboarding Celcoin e o
PostgreSQL local, isoladamente, não substituem o BFF mobile nem criam uma conta
de autenticação para os testes.

## Cenários de sandbox

No sandbox, o telefone determina o resultado da simulação da abertura da conta:

| Final do telefone | Resultado esperado |
|---|---|
| `1` | aprovado nos dois webhooks e conta criada |
| `2` | reprovado no primeiro webhook |
| `3` | primeira etapa aprovada e segunda etapa reprovada |
| outro dígito | comportamento próximo do fluxo real, sujeito à validação dos dados |

Use `phoneNumber` para PF e `contactNumber` para PJ. Depois de uma conta
aprovada no sandbox, adicione saldo antes de exercitar operações financeiras.

## Fluxo sem WebView

Quando a aplicação já coleta e valida os documentos, é possível enviar URLs
públicas em `files[]` na proposta e acompanhar o resultado por webhook/status.
As URLs devem ser acessíveis pela Celcoin, ter validade suficiente para o
processamento e apontar para documentos de até 10 MB. Esse fluxo não elimina as
validações de KYC, documentoscopia ou análise de risco.

## BC Protege+

A consulta ao BC Protege+ é obrigatória no processo de abertura de conta e é
realizada pela Celcoin durante o onboarding. O contrato público não expõe um
endpoint separado para iniciar, consultar ou simular essa verificação; por isso
o SDK não cria uma operação artificial para ela. O resultado aplicável à jornada
é refletido nos status e webhooks da proposta.

## Autenticação biométrica

```java
var auth = client.onboarding().createBiometricAuthentication(
    new CelcoinBiometricAuthRequest(
        "BIOMETRIC_LIVENESS", clientCode, fullName, cpf,
        Map.of("purpose", "PIX"), "3600", Map.of()));

var status = client.onboarding().listBiometricAuthentications(
    Map.of("biometricAuthId", auth.body().get("biometricAuthId")));
var files = client.onboarding().getBiometricFiles(
    String.valueOf(auth.body().get("biometricAuthId")), clientCode);
```

Use `BIOMETRIC_DOC_LIVENESS` quando a jornada exigir documento e selfie.
