# Checklist de qualidade, segurança e publicação mobile

## Antes do merge

- `npm ci` e `npm run validate` passam no frontend.
- Nenhum segredo, token, PAN, CVV, CPF ou CNPJ aparece em logs, bundle ou fixtures.
- Testes unitários e de componentes passam; contratos BFF usados pelo app estão versionados.

## Antes de homologar

- Validar login, KYC, Pix, boleto, notificações e logout em staging/sandbox.
- Executar testes E2E Android/iOS com massa isolada.
- Verificar permissões, acessibilidade, rede offline/lenta, retry e duplicidade.
- Executar análise de dependências, revisão de permissões e pentest mobile.

## Release e rollback

- Assinaturas Android/iOS vêm exclusivamente do secret store da CI.
- Publicar primeiro em distribuição interna/TestFlight/Play Internal Testing.
- Manter feature flags e versão mínima do BFF compatíveis.
- Rollback usa o artefato anterior; migrações de storage precisam ser retrocompatíveis.
- Aprovação das lojas e validação de produção são gates externos obrigatórios.
