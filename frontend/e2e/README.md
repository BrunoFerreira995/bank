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

O diretório é o ponto de entrada para Detox. A execução só pode ser
marcada como concluída após o fluxo passar em Android e iOS nos ambientes de
staging e sandbox.

## E2E iOS com Detox

### Pré-requisito: BFF em execução

Os testes iOS não chamam a API Celcoin diretamente: login, contas e operações
financeiras passam pelo BFF definido em `BFF_BASE_URL`. Para staging ou sandbox,
aponte essa variável para o ambiente correspondente e use a massa isolada
disponibilizada pelo ambiente. Para execução local, o BFF deve estar disponível
em `http://localhost:8080` antes de iniciar o Detox; o simulador iOS consegue
acessar esse serviço no Mac host.

O backend local precisa expor os contratos `/mobile/v1` usados pelo app. Subir
somente o SDK ou o banco de dados não substitui o BFF.

Checklist mínimo de autenticação:

- conta E2E provisionada pelo BFF;
- `POST /mobile/v1/session` retornando token ou desafio MFA;
- credenciais `E2E_USER_IDENTIFIER` e `E2E_USER_PASSWORD` exportadas;
- `E2E_MFA_CODE` exportado quando necessário;
- massa resetada por cenário e evidências arquivadas.

O host macOS também precisa ter Xcode, um runtime de simulador disponível e
`applesimutils` instalado:

```bash
brew tap wix/brew
brew install wix/brew/applesimutils
xcrun simctl list devices available
```

Se o `CoreSimulatorService` estiver indisponível, abra o Simulator/Xcode ou
reinicie o serviço antes de executar o Detox.

Instale as dependências nativas e crie a configuração local do BFF:

```bash
cp .env.e2e-ios.example .env.e2e-ios
npm run ios:pods
export E2E_USER_IDENTIFIER='massa-e2e-cpf-ou-cnpj'
export E2E_USER_PASSWORD='senha-da-massa-e2e'
npm run preflight:e2e:ios
npm run build:e2e:ios
npm run test:e2e:ios
```

`BFF_BASE_URL=https://bff-staging.example.invalid` é somente um placeholder
do exemplo e não pode ser usado para executar os testes. Substitua-o pelo
endpoint real do BFF de staging ou sandbox antes de iniciar a suíte; valide
também que o host resolve e responde aos contratos `/mobile/v1`.

Para uma massa que exige MFA, defina também `E2E_MFA_CODE`. O identificador e a
senha vêm exclusivamente do secret store da CI ou do ambiente local; não são
gravados em `.env.e2e-ios` versionado. O dispositivo pode ser alterado com
`DETOX_IOS_DEVICE='iPhone 17 Pro'`. Detox coleta screenshots por teste; a CI deve
publicar `frontend/artifacts` como evidência.

Os cenários de identidade e KYC também exigem a massa isolada definida por
`E2E_KYC_PF_CPF`, `E2E_KYC_PF_EMAIL`, `E2E_KYC_PF_PHONE`, `E2E_KYC_PJ_CNPJ`,
`E2E_KYC_PJ_EMAIL`, `E2E_KYC_PJ_PHONE`, `E2E_KYC_REPRESENTATIVE_NAME` e
`E2E_KYC_REPRESENTATIVE_CPF`. Esses valores vêm do secret store e não devem
ser adicionados a arquivos versionados.

Os fluxos Pix financeiros usam `E2E_PIX_KEY`, `E2E_PIX_AMOUNT`,
`E2E_PIX_BANK_CODE`, `E2E_PIX_BRANCH`, `E2E_PIX_ACCOUNT`, `E2E_PIX_DOCUMENT` e
`E2E_PIX_NEW_KEY`. Para QR Code, defina também `E2E_PIX_QR_CODE`. A massa deve
ser isolada e autorizada para testes; nenhuma dessas variáveis deve ser
versionada.

Para boletos, recargas e débitos veiculares, configure a massa isolada com
`E2E_BILL_CODE`, `E2E_TOPUP_OPERATOR_ID`, `E2E_TOPUP_PRODUCT_ID`,
`E2E_TOPUP_PHONE`, `E2E_TOPUP_AMOUNT`, `E2E_VEHICLE_DOCUMENT` e
`E2E_VEHICLE_RENAVAM`. Esses valores vêm do secret store e não devem ser
versionados.

## E2E web inicial

```bash
npx playwright install chromium
npm run test:e2e:web
```

Os cenários web mockam o BFF dentro do Playwright e cobrem login aceito,
dashboard, navegação para Pix e login recusado sem exposição de token.

Para executar todos os cenários web e salvar uma captura PNG do estado final de
cada fluxo em `frontend/artifacts/web-flows`, use:

```bash
npm run capture:e2e:web
```

Em macOS, se o Chromium headless gerar imagens brancas apesar de os testes
passarem, execute em modo visual:

```bash
WEB_SCREENSHOT_HEADED=1 npm run capture:e2e:web
```

Esse modo exige uma sessão gráfica ativa (Simulator/Xcode não é necessário).

O script retorna o mesmo código de saída do Playwright. Os nomes dos arquivos
incluem o identificador do teste e o título sanitizado, permitindo publicar o
diretório como evidência sem sobrescrever cenários com títulos diferentes.
