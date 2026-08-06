# Open Plus

O Open Plus é o portal web da Celcoin para cadastro da empresa, acesso ao
ecossistema Open Finance, contratação de produtos e administração das
aplicações. Ele não é um agregado HTTP do SDK: as operações do portal são
realizadas na interface web e dependem do usuário, da organização e do
contrato comercial.

## Jornada do portal

1. Cadastro da empresa e do administrador.
2. Login e configuração do segundo fator.
3. Acesso ao Hall Open Plus.
4. Criação da aplicação e seleção de produtos no catálogo.
5. Acesso ao painel da aplicação e às credenciais de sandbox.
6. Configuração do produto e execução dos testes de homologação.
7. Liberação e publicação em produção conforme aprovação contratual.

## Recursos administrativos

O portal cobre cadastro e acesso, Hall, aplicações, catálogo, membros da
equipe, menu de usuário, painel da aplicação, painel do produto, documentação,
credenciais de teste e Portal Configurações. O SDK consome as APIs liberadas
para a aplicação depois dessa configuração; ele não simula as telas nem
persiste credenciais do portal.

Para produtos Open Finance, a configuração do Open Keys disponibiliza ainda o
Dashboard, demonstração das jornadas e gestão das transações. Essas funções
continuam sendo administradas pelo portal, enquanto as jornadas transacionais
estão implementadas nos agregados `openFinance()`, `itp()`, `jsr()` e
`pixAuto()`.

## Referências oficiais

- [Open Plus](https://developers.celcoin.com.br/docs/open-plus)
- [Cadastro e acesso](https://developers.celcoin.com.br/docs/cadastro-e-acesso)
- [Gestão de aplicações](https://developers.celcoin.com.br/docs/gest%C3%A3o-de-aplica%C3%A7%C3%B5es)
- [Catálogo de produtos](https://developers.celcoin.com.br/docs/cat%C3%A1logo-de-produtos)
- [Gestão de membros](https://developers.celcoin.com.br/docs/gest%C3%A3o-de-membros-da-equipe)
- [Painel da aplicação](https://developers.celcoin.com.br/docs/painel-de-gest%C3%A3o-da-aplica%C3%A7%C3%A3o)
- [Portal Configurações](https://developers.celcoin.com.br/docs/portal-configura%C3%A7%C3%B5es)
