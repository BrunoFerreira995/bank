# Celcoin Spring SDK + Frontend React Native

SDK Spring Boot para integrar produtos Celcoin em aplicações Java 21, com um
frontend React Native CLI compartilhado entre Android, iOS e Web.

O projeto tem duas partes:

```text
React Native CLI / Web  →  API BFF da instituição  →  Celcoin Spring SDK  →  APIs Celcoin
```

O aplicativo nunca acessa a Celcoin diretamente. `clientSecret`, mTLS, SFTP,
webhooks, antifraude, PLD e decisões financeiras permanecem no backend/BFF.

## Comece aqui

### Requisitos

- Java 21;
- Node.js 22.13+;
- Docker e Docker Compose;
- Android Studio/SDK para Android;
- Xcode e CocoaPods para iOS;
- Maven Wrapper (`./mvnw`) incluído no repositório.

### Backend local

```bash
cp .env.example .env
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
curl http://localhost:8080/actuator/health
```

Credenciais reais nunca devem ser versionadas. O BFF deve apontar para este
backend, e não para a API Celcoin diretamente.

### Frontend mobile

```bash
cd frontend
cp .env.example .env
npm install
npm run validate
npm start
```

Em outro terminal:

```bash
npm run android
# ou
npm run ios
```

### Frontend Web

```bash
cd frontend
cp .env.web.example .env
npm install
npm run web
```

Build de produção:

```bash
npm run web:build
```

Web usa `react-native-web` e Vite. O adaptador de sessão web é somente uma
compatibilidade de navegador; autenticação, segredos e operações financeiras
continuam no BFF.

## Configuração do SDK

Exemplo de configuração Spring:

```yaml
celcoin:
  enabled: true
  environment: sandbox
  base-url: ${CELCOIN_BASE_URL}
  client-id: ${CELCOIN_CLIENT_ID}
  client-secret: ${CELCOIN_CLIENT_SECRET}
  token-path: /v5/token
  connect-timeout: 5s
  read-timeout: 20s
  token-refresh-margin: 60s
  demo-enabled: false
  webhook:
    secret: ${CELCOIN_WEBHOOK_SECRET:}
```

O token OAuth é obtido em `POST /v5/token`, armazenado em cache Caffeine e
renovado antes do vencimento. Operações financeiras usam idempotência
persistente e os webhooks são validados/deduplicados no backend.

## Usar como dependência

```xml
<dependency>
  <groupId>com.brunopedraca</groupId>
  <artifactId>celcoin-spring-sdk</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

```java
CelcoinTokenResponse token = celcoinClient.authentication().getToken();
CelcoinBalanceResponse balance = celcoinClient.accounts().getBalance(accountId);
CelcoinPixPaymentResponse payment = celcoinClient.pix().cashOut(request);
CelcoinBoletoResponse boleto = celcoinClient.boletos().issue(request);
```

## Capacidades

- BaaS/Core Banking, contas, saldo, extrato e movimentações;
- onboarding KYC PF/PJ e documentos;
- Pix Cash-in/Cash-out, chaves, cobranças, devoluções e agendamento;
- boletos, recargas, débitos veiculares e transferências;
- cartões, crédito, consignado e Escrow;
- Open Finance, ITP, Brick Bank/Insurance e sweeping;
- webhooks, idempotência, rate limit, mTLS e auditoria;
- frontend com login, KYC, conta, Pix, pagamentos, produtos financeiros,
  Open Finance, suporte e notificações;
- frontend Web com a mesma base React Native.

Produtos dependentes de contratação ou contrato específico permanecem
sinalizados no checklist e não têm endpoints inventados.

## Testes e validação

Backend:

```bash
./mvnw clean verify
```

Frontend nativo e web:

```bash
cd frontend
npm run validate
npm run web:build
```

E2E Web:

```bash
npx playwright install chromium
npm run test:e2e:web
```

Os E2E web atuais cobrem login aceito, dashboard, navegação para Pix e login
recusado sem exposição de token. A matriz completa está em
[`docs/frontend-e2e-coverage.md`](docs/frontend-e2e-coverage.md).

A suíte E2E iOS com Detox cobre atualmente sessão, identidade/KYC, conta e
movimentações, Pix, boletos, recargas e débitos. A execução operacional ainda
depende de simulador/dispositivo, BFF de staging ou sandbox, massa isolada e
evidências arquivadas.

## Documentação essencial

| Documento | Conteúdo |
|---|---|
| [`docs/celcoin-implementation-checklist.md`](docs/celcoin-implementation-checklist.md) | Status completo por produto |
| [`docs/react-native.md`](docs/react-native.md) | Arquitetura BFF e frontend |
| [`docs/frontend-e2e-coverage.md`](docs/frontend-e2e-coverage.md) | Matriz de cobertura E2E |
| [`docs/frontend-100-percent.md`](docs/frontend-100-percent.md) | Critérios de definição de 100% |
| [`docs/mobile-release-checklist.md`](docs/mobile-release-checklist.md) | Segurança, release e rollback |
| [`frontend/README.md`](frontend/README.md) | Desenvolvimento React Native/Web |
| [`frontend/e2e/test-matrix.md`](frontend/e2e/test-matrix.md) | Cenários E2E por domínio |
| [`docs/examples.md`](docs/examples.md) | Exemplos de uso do SDK |
| [`docs/external-dependencies.md`](docs/external-dependencies.md) | Dependências externas e contratuais |

## Segurança

- segredos Celcoin ficam no backend;
- tokens não são registrados em logs;
- CPF/CNPJ, credenciais e dados de cartão são mascarados;
- PAN/CVV completos não são armazenados no frontend;
- webhooks exigem assinatura, limite de payload e deduplicação;
- mutações financeiras usam `Idempotency-Key`;
- rate limit respeita `Retry-After` e headers de controle;
- mTLS é configurável via `celcoin.ssl.enabled=true`;
- o frontend possui secret scan, redaction de observabilidade e gate de
  definição de 100%.

## CI/CD

- `.github/workflows/ci.yml`: build, testes, Spotless, JaCoCo e artefato Java;
- `.github/workflows/frontend-ci.yml`: validação TypeScript, Jest, Prettier,
  gate de segurança e E2E web;
- assinaturas Android/iOS devem vir do secret store da CI;
- TestFlight, Play Internal Testing, pentest e aprovação das lojas são gates
  externos de release.

## Status e próximos gates

O status detalhado está no checklist. A fundação do SDK, sandbox, frontend
React Native/Web e os primeiros E2E web estão implementados. Ainda exigem
execução operacional: E2E Android/iOS em staging/sandbox, pentest mobile,
homologação dos produtos contratados e publicação nas lojas.
