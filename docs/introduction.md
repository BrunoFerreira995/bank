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
| Contas BaaS & Core Banking | `banking` | Implementado |
| Pix (cash-in, cash-out, chaves, split) | `pix` | Estrutura preparada |
| Pix Automático (recorrência) | `pixauto` | Implementado |
| Pix Inteligente (Sweeping Accounts) | `sweeping` | Implementado |
| Pix Indireto | `indirectPix` | Implementado |
| CNAB | `cnab` | Implementado |
| Open Finance as a Service | `openFinance` | Implementado |
| Jornada Sem Redirecionamento / FIDO | `jsr` | Implementado |
| Boletos e cobranças | `boleto` | Estrutura preparada |
| Cartões | `cards` | Estrutura preparada |
| Subadquirência e AaaS | `acquiring` | Estrutura preparada |
| Webhooks BaaS | `webhook` | Implementado |
| Idempotência por operação | `common.idempotency` | Implementado |
| Rate limit / headers de controle | `common.http` | Implementado |
| mTLS | `common.http` | Implementado |

Alguns produtos ainda permanecem como estrutura preparada quando a Celcoin não
publicou o endpoint ou payload necessário para o contrato do SDK.

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
