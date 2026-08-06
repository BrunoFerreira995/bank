# Frontend React Native para Celcoin

## Arquitetura obrigatória

O aplicativo React Native CLI deve conversar somente com uma API BFF da instituição.
O fluxo recomendado é:

```text
React Native -> BFF/API da instituição -> Celcoin Spring SDK -> APIs Celcoin
                                      -> Webhooks, filas, banco e auditoria
```

O app nunca deve receber `clientSecret`, certificado mTLS, credenciais SFTP,
tokens administrativos ou regras antifraude. O BFF é responsável por autenticar
o usuário, aplicar autorização por conta/produto, gerar idempotência, validar
callbacks, consumir webhooks e esconder os contratos internos da Celcoin.

## Camadas sugeridas

- `src/app`: bootstrap, navegação, providers e feature flags.
- `src/core`: cliente HTTP, sessão, storage seguro, erros, telemetria e design system.
- `src/features`: `onboarding`, `accounts`, `pix`, `boletos`, `topups`, `vehicles`,
  `cards`, `credit`, `open-finance`, `notifications` e `support`.
- `src/native`: biometria, câmera, QR Code, deep links, FIDO2/WebAuthn, NFC e push.
- `src/test`: unitários, contrato, componentes e E2E.

## Regras de segurança

1. Guardar somente tokens de sessão de curta duração no Keychain/Keystore.
2. Não registrar CPF, CNPJ, PAN, CVV, tokens, documentos ou respostas completas.
3. Fazer confirmação visual do favorecido antes de uma operação financeira.
4. Usar idempotency key gerada por operação no BFF; retry não pode duplicar pagamento.
5. Não alterar saldo ou status financeiro com dados recebidos diretamente de push/deep link.
6. Validar no BFF toda transição recebida do app e toda URL de retorno.
7. Aplicar proteção de tela e limpeza de clipboard para dados sensíveis.

## Contrato mínimo do BFF

O BFF deve expor recursos versionados, por exemplo:

- `POST /mobile/v1/session` e `DELETE /mobile/v1/session`;
- `GET /mobile/v1/accounts` e `GET /mobile/v1/accounts/{id}/statement`;
- `POST /mobile/v1/pix/payments` e `GET /mobile/v1/pix/payments/{id}`;
- `POST /mobile/v1/boletos/payments`;
- `POST /mobile/v1/onboarding` e `GET /mobile/v1/onboarding/{id}`;
- `GET /mobile/v1/open-finance/brands` e `POST /mobile/v1/open-finance/consents`;
- `GET /mobile/v1/notifications` e `/mobile/v1/support/tickets` quando contratados.

Esses caminhos são uma proposta de contrato do frontend, não endpoints Celcoin
adicionais. Devem ser versionados e documentados em OpenAPI antes do início do
desenvolvimento mobile.

## Critério de aceite

O frontend só pode ser considerado 100% quando todas as funcionalidades
contratadas tiverem telas e estados de UX, o BFF tiver testes de contrato, o
app tiver testes E2E Android/iOS, os fluxos de deep link/biometria/FIDO2 forem
validados em dispositivos reais e os artefatos de segurança e publicação
estiverem aprovados.
