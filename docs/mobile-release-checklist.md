# Checklist de qualidade, segurança e publicação mobile

## Antes do merge

- `npm ci` e `npm run validate` passam no frontend.
- Nenhum segredo, token, PAN, CVV, CPF ou CNPJ aparece em logs, bundle ou fixtures.
- Testes unitários e de componentes passam; contratos BFF usados pelo app estão versionados.
- A suíte Detox iOS está configurada para sessão, KYC, conta, Pix, boletos,
  recargas e débitos em `frontend/e2e/ios`.

## Antes de homologar

- Validar login, KYC, Pix, boleto, notificações e logout em staging/sandbox.
- Executar testes E2E Android/iOS com massa isolada.
- Para iOS, executar `npm run build:e2e:ios` e `npm run test:e2e:ios` após
  `npx pod-install ios`, usando somente as variáveis `E2E_*` do secret store.
- Arquivar screenshots, logs, dispositivo, versão do app, versão do BFF e
  `correlationId` por cenário; implementação da suíte não substitui evidência.
- Verificar permissões, acessibilidade, rede offline/lenta, retry e duplicidade.
- Executar análise de dependências, revisão de permissões e pentest mobile.

## Release e rollback

- Assinaturas Android/iOS vêm exclusivamente do secret store da CI.
- Publicar primeiro em distribuição interna/TestFlight/Play Internal Testing.
- Manter feature flags e versão mínima do BFF compatíveis.
- Rollback usa o artefato anterior; migrações de storage precisam ser retrocompatíveis.
- Aprovação das lojas e validação de produção são gates externos obrigatórios.
