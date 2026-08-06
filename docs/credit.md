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
- consulta e envio de assinaturas da CCB;
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
