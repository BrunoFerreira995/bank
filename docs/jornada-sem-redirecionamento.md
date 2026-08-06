# Jornada Sem Redirecionamento

O módulo `jsr` implementa o fluxo Open Finance em que o usuário autoriza um
vínculo de dispositivo e conclui o Pix sem sair da ITP. O vínculo usa FIDO2 /
WebAuthn e deve ser tratado como uma credencial sensível do dispositivo.

## Fluxo

1. `client.jsr().createEnrollment(...)` cria o vínculo.
2. A aplicação conclui a autorização da detentora e encaminha o retorno para
   `processCallback(...)`.
3. A aplicação chama `fidoRegistrationOptions(...)`, executa
   `navigator.credentials.create(...)` no dispositivo e envia o resultado com
   `registerFido(...)`.
4. `createPaymentInitiation(...)` cria a iniciação de pagamento v4.
5. `fidoSignOptions(...)` gera o desafio, a aplicação executa
   `navigator.credentials.get(...)` e `authorizeFido(...)` autoriza a operação.
6. `createPix(...)` efetiva o PIX v4.

O limite e as regras de pagamento sem redirecionamento são definidos pela
Celcoin e pela detentora. A aplicação deve tratar a resposta como assíncrona e
acompanhar webhooks/status conforme o contrato contratado.

## Exemplo

```java
var enrollment = client.jsr().createEnrollment(
    new CelcoinJsrDtos.EnrollmentRequest(riskSignals, "MOBILE", null), idempotencyKey);

var options = client.jsr().fidoRegistrationOptions(enrollmentId,
    new CelcoinJsrDtos.FidoOptionsRequest("app.example.com", "ANDROID"));

var check = client.jsr().validateFidoBiometry(webAuthnRegistration);
if (!check.valid()) throw new IllegalArgumentException(check.reason());

client.jsr().registerFido(enrollmentId,
    new CelcoinJsrDtos.FidoRegistrationRequest(webAuthnRegistration), idempotencyKey);
```

`validateFidoBiometry` faz apenas a validação estrutural do retorno WebAuthn.
Biometria, PIN, Face ID e a verificação criptográfica da assinatura são
responsabilidades da API WebAuthn do dispositivo/navegador e da Celcoin; não é
seguro tentar reproduzi-las no SDK Java.
