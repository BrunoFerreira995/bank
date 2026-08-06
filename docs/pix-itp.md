# Pix Instantâneo — ITP

O módulo `itp` implementa a jornada Open Finance com redirecionamento para um
pagamento Pix único. Cada consentimento autoriza uma única iniciação de
pagamento.

```java
var consent = client.itp().createConsent(
    new CelcoinItpDtos.ConsentRequest(
        brandId, callbackUrl, cpfPagador, cpfCnpjRecebedor,
        "PESSOA_JURIDICA", nomeRecebedor, new BigDecimal("1.15"),
        LocalDate.now(), "DICT", chavePix, creditorAccount, null),
    "itp-consent-1");

// Redirecione para consent.get("authorizationUrl") e, no callback:
client.itp().processCallback(new CelcoinItpDtos.CallbackRequest(code, state, idToken));

client.itp().createPix(paymentInitiationId,
    new CelcoinItpDtos.PixRequest(Map.of()), "itp-pix-1");

var current = client.itp().getPaymentInitiation(paymentInitiationId);
```

O callback deve ser enviado imediatamente após o retorno da detentora. O
pagamento é assíncrono: acompanhe `PDNG`, `ACSP`, `ACSC` ou `RJCT` por webhook
e use consulta como contingência. O receptor comum em `/webhooks/celcoin`
aceita o `x-celcoin-event-id`, valida assinatura e deduplica os eventos.

`ItpStateMachine` expõe as transições válidas dos estados do consentimento e
do pagamento. `ItpPaymentErrors` classifica os principais motivos `RJCT` como
retriáveis ou definitivos; rejeições retriáveis devem criar novo consentimento,
pois o consentimento ITP é de uso único.

Endpoints:

- `POST /baas/v1/open/itp/payment-initiation`
- `POST /baas/v1/open/itp/payment-initiation/callback`
- `POST /baas/v1/open/itp/payment-initiation/{id}/pix`
- `GET /baas/v1/open/itp/payment-initiation/{id}`

Consulte o [contrato oficial de criação](https://developers.celcoin.com.br/docs/itp-criar-consentimento-payment-initiation),
a [máquina de estados](https://developers.celcoin.com.br/docs/itp-m%C3%A1quina-de-estados-pix-itp)
e os [erros de pagamento](https://developers.celcoin.com.br/docs/itp-poss%C3%ADveis-erros-de-pagamento)
antes de habilitar produção.
