# Celcoin Mobile

Aplicativo React Native CLI para consumo da API BFF da instituição. O app
não chama as APIs Celcoin diretamente e não contém `clientSecret`, mTLS,
credenciais SFTP ou regras antifraude.

## Requisitos

- Node.js `22.13+`;
- Android Studio/SDK para Android;
- Xcode e CocoaPods para iOS;
- CocoaPods instalado e configurado para iOS.
- Navegador moderno para a distribuição web.

## Inicialização

```bash
cp .env.example .env
npm install
npm run validate
npm start
```

O baseline usa React Native 0.86, a New Architecture e os projetos nativos
Android/iOS gerados pelo React Native CLI.

## Web

O mesmo código compartilhado também pode ser executado com `react-native-web`:

```bash
cp .env.web.example .env
npm run web
npm run web:build
```

Na web, os valores usam o prefixo `VITE_`. O adaptador web de sessão é apenas
uma compatibilidade de navegador; autenticação, segredos, mTLS e operações
financeiras continuam no BFF.

## Configuração

`BFF_BASE_URL` aponta para o BFF, nunca para a API Celcoin. O BFF
deve implementar os contratos descritos em `docs/react-native.md`, validar a
sessão do usuário e encaminhar operações ao `CelcoinClient` no backend.

## Segurança

Não coloque segredos em `.env` público, no bundle JavaScript ou em logs. Tokens
são persistidos exclusivamente por `react-native-keychain`; webhooks, idempotência
definitiva, mTLS, auditoria e decisões antifraude são responsabilidades do
backend.
