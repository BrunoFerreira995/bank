# Brick Bank e Brick Insurance

## Brick Bank

O Brick Bank é a camada Celcoin para entrada, coordenação e manutenção de uma
instituição no ecossistema Open Finance. No SDK, o papel publicado e
implementado é o de instituição receptora de dados por meio de
`client.openFinance()`:

- consulta de marcas e participantes;
- criação de consentimento;
- direcionamento para a transmissora;
- processamento do callback;
- consulta de resources;
- consumo paginado de clientes, contas, transações, crédito e investimentos.

Esse fluxo cobre a integração de recepção descrita em
[`docs/open-finance.md`](open-finance.md). A aplicação contratante continua
responsável pela experiência do usuário, finalidade, escopo, prazo e termos do
consentimento.

## Papéis dependentes de contrato

Os papéis de Brick Bank como transmissora de dados ou detentora de contas
exigem que a instituição disponibilize APIs regulatórias próprias, autenticação,
validação de titularidade e confirmação de consentimento. Os endpoints
encontrados para login/aprovação da detentora usam host e credenciais próprios
do ambiente Brick, portanto não são tratados como rotas BaaS genéricas pelo
SDK.

Brick Insurance também permanece como integração contratual específica. Não há
contrato público suficiente para implementar operações de recepção ou
transmissão sem acoplar o SDK a um produto ou ambiente não configurado.

## Referências

- [Sobre o Brick Bank](https://developers.celcoin.com.br/docs/sobre-o-bricks)
- [Aprovação no detentor de contas](https://developers.celcoin.com.br/reference/post_api-client-name-consents-v1-interactions-consent-interaction-id-consent)
- [Login no detentor de contas](https://developers.celcoin.com.br/reference/post_api-client-name-consents-v1-interactions-login-interaction-id-login)
- [Consentimentos transmitidos](https://developers.celcoin.com.br/reference/get_api-management-consents-v1-consents-payment-transmitions)
