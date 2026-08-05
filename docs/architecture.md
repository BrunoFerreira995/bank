# Arquitetura

O SDK usa uma arquitetura modular por domínio:

- `auth`: geração, cache e renovação de token.
- `common`: exceções, HTTP, idempotência, modelos compartilhados e sanitização.
- `banking`: gestão de contas e operações BaaS.
- `pix`: cash-in, cash-out, chaves, agendamento e split.
- `pixauto`: Pix Automático (consentimento, agendamento, liquidação, cancelamento e jornada recebedora).
- `boleto`: emissão, consulta, cancelamento e PDF.
- `webhook`: recebimento, persistência, deduplicação e reprocessamento.
- `demo`: endpoints de exemplo desativáveis.

As aplicações consumidoras dependem da fachada `CelcoinClient` ou das interfaces por domínio, facilitando mocks em testes.

Contratos não confirmados não possuem URLs fabricadas. Nesses casos, os clientes lançam `CelcoinIntegrationException` com mensagem explícita.

## Idempotência

O `CelcoinIdempotencyService` persiste um registro por `Idempotency-Key` e
operação (endpoint) na tabela `celcoin_idempotency_record`. Uma chave reutilizada
com request diferente gera `CelcoinConflictException`; um request concluído é
reproduzido a partir do `response_body` salvo sem nova chamada à Celcoin.

## Rate limit

Em respostas `429`, o `CelcoinHttpClient` lê os headers `Retry-After` e
`X-RateLimit-*` e lança `CelcoinRateLimitException`, expondo
`rateLimit()` e `retryAfter()` para o chamador decidir a política de espera.

## mTLS

Com `celcoin.ssl.enabled=true`, o `NettyCelcoinSslContextProvider` carrega o
keystore e o truststore (PKCS12/JKS) e aplica o contexto TLS no `WebClient`.
Aplicações podem substituir o bean `CelcoinSslContextProvider` para um controle
completo da configuração TLS.
