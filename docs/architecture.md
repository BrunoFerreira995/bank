# Arquitetura

O SDK usa uma arquitetura modular por domínio:

- `auth`: geração, cache e renovação de token.
- `common`: exceções, HTTP, idempotência, modelos compartilhados e sanitização.
- `banking`: gestão de contas e operações BaaS.
- `pix`: cash-in, cash-out, chaves, agendamento e split.
- `boleto`: emissão, consulta, cancelamento e PDF.
- `webhook`: recebimento, persistência, deduplicação e reprocessamento.
- `demo`: endpoints de exemplo desativáveis.

As aplicações consumidoras dependem da fachada `CelcoinClient` ou das interfaces por domínio, facilitando mocks em testes.

Contratos não confirmados não possuem URLs fabricadas. Nesses casos, os clientes lançam `CelcoinIntegrationException` com mensagem explícita.
