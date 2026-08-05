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

- **Autenticação biométrica e prova de vida** não possuem endpoint dedicado no
  contrato oficial de Onboarding KYC: o envio da `SELFIE` na proposta alimenta
  o fluxo de biometria facial. A solução de Autenticação Biométrica da Celcoin
  é um produto separado.
- **Idempotência**: `createPersonAccount` e `createBusinessAccount` aceitam
  `idempotencyKey`, aplicada via cabeçalho `Idempotency-Key`.
