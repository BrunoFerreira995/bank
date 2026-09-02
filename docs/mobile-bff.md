# Mobile BFF v1

The mobile backend-for-frontend owns only the `/mobile/v1/**` surface. It is deliberately split by domain (`bff.v1.accounts`, `bff.v1.pix`, and future domains) so a controller or service never becomes a generic SDK proxy.

## Contracts

The initial version exposes:

- `GET /mobile/v1/accounts/{accountId}/balance`
- `POST /mobile/v1/pix/decode`

Requests and responses are BFF-owned versioned types. SDK DTOs stay behind domain services, which use `CelcoinClient` and its `*Operations` interfaces. Errors use the `MobileV1Error` contract with a stable `code`, `message`, `correlationId`, `timestamp`, and field violations when applicable.

## Correlation and audit

Supply an optional `X-Correlation-Id` with up to 120 letters, numbers, dots, underscores, or hyphens. The BFF generates one otherwise, returns it in every response, adds it to logging MDC, and records method, path, status, duration, and correlation id in `mobile_request_audit`. Request and response payloads are intentionally not stored.

## Configuration and API documentation

`mobile.bff.enabled` controls the BFF independently from the Celcoin SDK. It is configured for local, test, staging, and production profiles; production leaves Swagger UI disabled and lets `MOBILE_BFF_OPENAPI_ENABLED` decide whether the OpenAPI JSON endpoint is published.

With the BFF enabled, the OpenAPI contract is available at `/v3/api-docs/mobile-v1` and Swagger UI at `/swagger-ui/index.html` (except production by default).

## Session and identity

The session API provides `POST /mobile/v1/session`, `/mfa`, `/refresh`, `/recovery`, and `DELETE /mobile/v1/session`. Access and refresh credentials are 256-bit opaque tokens; only their SHA-256 hashes are persisted. Refresh rotates both credentials and revoking a session invalidates both immediately.

Users, their authorized account ids, sessions, and short-lived MFA challenges are stored in the BFF database. Public account routes require a bearer token and check ownership before calling Celcoin. Password verification uses PBKDF2-HMAC-SHA-256 with 210,000 iterations and a constant-work missing-user path. MFA is TOTP with a five-attempt, five-minute challenge. Recovery always returns `202 Accepted` with the same neutral response; delivery of recovery instructions must be integrated with an approved notification provider before enabling it for customers.

User provisioning and secure lifecycle of the encrypted MFA secret are administrative/back-office responsibilities; no public registration or account-linking route is exposed by this BFF.

## Authorization

Every account operation must call `MobileAccountAuthorizationService` with its required `READ`, `WRITE`, or `RISK` permission before contacting the SDK. `CUSTOMER` may act only on an account linked to that user. `SUPPORT` may only read accounts for which it has an explicit grant; `OPERATIONS` may read/write explicit grants; and `ADMIN` also requires an explicit account grant. This deliberately prevents privileged roles from becoming unrestricted account access.

`RISK` operations additionally call `requireRisk`, which requires a fresh TOTP step-up. The client first posts its code to `POST /mobile/v1/session/step-up`; elevation lasts five minutes and does not survive refresh-token rotation.
