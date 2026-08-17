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

Instale as dependências nativas e crie a configuração local do BFF:

```bash
cp .env.e2e-ios.example .env.e2e-ios
npx pod-install ios
export E2E_USER_IDENTIFIER='massa-e2e-cpf-ou-cnpj'
export E2E_USER_PASSWORD='senha-da-massa-e2e'
npm run build:e2e:ios
npm run test:e2e:ios
```

Para uma massa que exige MFA, defina também `E2E_MFA_CODE`. O identificador e a
senha vêm exclusivamente do secret store da CI ou do ambiente local; não são
gravados em `.env.e2e-ios` versionado. O dispositivo pode ser alterado com
`DETOX_IOS_DEVICE='iPhone 15'`. Detox coleta screenshots por teste; a CI deve
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
