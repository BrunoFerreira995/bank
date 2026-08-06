# cel_credit

O módulo `credit` utiliza a autenticação própria da Plataforma de Crédito
Celcoin, separada da autenticação BaaS. O token é obtido por OAuth 2.0
`client_credentials` em `sandbox.auth.flowfinance.com.br` e as operações usam
`sandbox.platform.flowfinance.com.br`.

Operações disponíveis no SDK:

- autenticação da plataforma;
- criação, consulta e listagem de solicitações;
- simulação de produto (`/banking/originator/products/{id}/preview`);
- variáveis personalizadas;
- consulta e envio de assinaturas da CCB.

Configure `CELCOIN_CREDIT_CLIENT_ID` e `CELCOIN_CREDIT_CLIENT_SECRET` antes de
usar `celcoinClient.credit()`. O token é reutilizado até próximo do vencimento.
