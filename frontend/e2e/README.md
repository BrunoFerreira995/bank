# E2E mobile

Os E2E devem executar em Android e iOS contra um BFF de staging com massa
isolada. A matriz completa está em [test-matrix.md](./test-matrix.md).

Regras obrigatórias:

- nunca usar credenciais de produção ou dados reais;
- resetar a massa por cenário e preservar o `correlationId` da execução;
- validar loading, vazio, erro, retry, sucesso e logout;
- confirmar que operações financeiras retornam idempotência e comprovante;
- validar deep links, callbacks, timeout, offline e sessão expirada;
- guardar vídeo/log/screenshots e resultado por dispositivo como evidência.

O diretório é o ponto de entrada para Detox/Appium. A execução só pode ser
marcada como concluída após o fluxo passar em Android e iOS nos ambientes de
staging e sandbox.

## E2E web inicial

```bash
npx playwright install chromium
npm run test:e2e:web
```

Os cenários web mockam o BFF dentro do Playwright e cobrem login aceito,
dashboard, navegação para Pix e login recusado sem exposição de token.
