# Autenticação

Contrato implementado:

- Método: `POST`
- Path: `/v5/token`
- Campos enviados: `client_id`, `client_secret`, `grant_type=client_credentials`

O token é cacheado em memória com Caffeine. A renovação ocorre antes do vencimento usando `celcoin.token-refresh-margin`. Uma trava local evita múltiplas renovações simultâneas.

O `ExchangeFilterFunction` de autenticação só é aplicado ao WebClient de APIs autenticadas. O cliente de token usa um WebClient separado e nunca recebe `Authorization: Bearer`.

Nenhum token ou segredo é incluído em exceções sanitizadas ou logs planejados.
