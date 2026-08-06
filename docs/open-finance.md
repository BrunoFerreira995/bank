# Open Finance as a Service

O módulo `openFinance()` cobre a recepção de dados Open Finance: diretório de
marcas, consentimento, callback, resources e consultas de dados cadastrais,
contas, crédito e investimentos.

## Fluxo

1. Liste marcas com `listBrands()` e mostre nome/logo somente conforme as
   regras do Diretório Open Finance.
2. Crie o consentimento com `createConsent` e redirecione para a URL retornada.
3. Processe `code`, `state` e `id_token` com `processCallback`.
4. Consulte `listResources` e, depois, os domínios autorizados.

As respostas permanecem como `Map<String, Object>` porque os schemas regulatórios
possuem versões e campos condicionais. Use `page`, `page-size` e
`pagination-key` nas consultas paginadas e respeite os limites mensais por
cliente, endpoint e consentimento.

Domínios implementados:

- Customers PF/PJ: identificação, qualificação e relacionamento;
- Accounts: contas e transações;
- Loans e financings;
- Renda fixa bancária e de crédito;
- Renda variável, Tesouro Direto e fundos.

O consentimento e os dados exigem scopes específicos (`CUSTOMERS_*`,
`ACCOUNTS_*`, `LOANS_READ`, `FINANCINGS_READ` e scopes de investimentos). O
cliente não tenta contornar autorização; respostas `401`, `403`, `404` e `429`
devem ser tratadas pelo consumidor.

Logo, nome amigável, descrição e termos devem ser apresentados conforme o
Diretório Open Finance Brasil e o Guia de Experiência do Usuário. Não baixe ou
altere logos localmente sem observar as regras de uso da marca.
