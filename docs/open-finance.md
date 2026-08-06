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

## Compartilhamento de Dados: etapas da jornada

O fluxo implementado cobre as responsabilidades da Instituição Receptora e o
consumo autorizado na Transmissora:

1. **Consentimento** — `listBrands`, `getBrand` e `createConsent` permitem
   selecionar a marca e enviar finalidade, usuário e permissões em `data`.
2. **Direcionamento** — a resposta de `createConsent` contém a URL de
   autorização; a aplicação deve redirecionar o cliente para ela.
3. **Autenticação e confirmação** — acontecem no ambiente da instituição
   transmissora. O SDK não coleta credenciais nem simula aprovação.
4. **Callback/redirecionamento** — após o retorno, envie `code`, `state` e
   `id_token` por `processCallback` para obter o resultado/autorização.
5. **Consumo** — use `listResources` e os métodos de domínio com paginação,
   mantendo os scopes e o consentimento associados.

As APIs de dados são deliberadamente expostas como `Map<String, Object>` para
acomodar versões regulatórias e campos condicionais. O método `getData` aceita
somente caminhos `/baas/v1/open/dat/`, evitando que o consumidor use o cliente
para acessar rotas arbitrárias.

### Transmissora e receptora

Na integração Open Keys Data, a aplicação contratante inicia o consentimento e
recebe os dados; a autenticação e a confirmação pertencem à instituição
transmissora. A interface da aplicação deve apresentar finalidade, instituição,
escopo e prazo do consentimento antes do redirecionamento, e tratar revogação e
expiração sem reutilizar um consentimento inválido.

Logo, nome amigável, descrição e termos devem ser apresentados conforme o
Diretório Open Finance Brasil e o Guia de Experiência do Usuário. Não baixe ou
altere logos localmente sem observar as regras de uso da marca.

Referências oficiais: [fluxo de compartilhamento de dados](https://developers.celcoin.com.br/docs/open-keys-data-fluxo-de-utiliza%C3%A7%C3%A3o-da-api-de-dados),
[consentimento](https://developers.celcoin.com.br/docs/consentimento),
[autenticação](https://developers.celcoin.com.br/docs/etapa-3-autentica%C3%A7%C3%A3o),
[confirmação](https://developers.celcoin.com.br/docs/etappa-4-confirma%C3%A7%C3%A3o) e
[códigos de resposta](https://developers.celcoin.com.br/docs/consumo-de-dados-open-finance).
