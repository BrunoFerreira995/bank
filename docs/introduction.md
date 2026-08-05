# Introdução

## O que é o SDK

O Celcoin Spring SDK é uma biblioteca Java 21 que encapsula o acesso às APIs da
Celcoin (BaaS, Pix, boletos, cartões, subadquirência, onboarding KYC, webhooks e
autenticação). Ele funciona como starter Spring Boot, oferecendo configuração
automática via `celcoin.*` e uma fachada única `CelcoinClient` para as operações
por domínio.

## Produtos cobertos

| Produto | Módulo | Estado |
| --- | --- | --- |
| Autenticação (`POST /v5/token`) | `auth` | Implementado |
| Onboarding KYC (PF/PJ) | `onboarding` | Estrutura preparada |
| Contas BaaS & Core Banking | `banking` | Estrutura preparada |
| Pix (cash-in, cash-out, chaves, split) | `pix` | Estrutura preparada |
| Pix Automático (recorrência) | `pixauto` | Estrutura preparada |
| Boletos e cobranças | `boleto` | Estrutura preparada |
| Cartões | `cards` | Estrutura preparada |
| Subadquirência e AaaS | `acquiring` | Estrutura preparada |
| Webhooks BaaS | `webhook` | Implementado |
| Idempotência por operação | `common.idempotency` | Implementado |
| Rate limit / headers de controle | `common.http` | Implementado |
| mTLS | `common.http` | Implementado |

"Estrutura preparada" significa que as interfaces públicas, os DTOs mínimos e os
clientes já existem, mas os métodos que dependem de URLs e payloads oficiais da
Celcoin lançam uma exceção controlada até que os contratos sejam anexados ao
projeto.

## Como o SDK funciona

1. `CelcoinTokenService` autentica na Celcoin, mantém o token em cache Caffeine e
   renova antes do vencimento.
2. `CelcoinHttpClient` executa as chamadas autenticadas, aplica retry para erros
   transitórios, interpreta os headers de rate-limit e, quando configurado,
   registra as operações idempotentes no banco.
3. `CelcoinWebhookService` recebe eventos, valida assinatura HMAC, deduplica e
   persiste.
4. Aplicações consomem a fachada `CelcoinClient` ou as interfaces por domínio.

## Exemplos

Veja `docs/examples.md` para usos por produto.
