package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookReceipt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class CelcoinWebhookController {
    private final CelcoinWebhookService service;

    public CelcoinWebhookController(CelcoinWebhookService service) {
        this.service = service;
    }

    @PostMapping("/webhooks/celcoin")
    public ResponseEntity<CelcoinWebhookReceipt> receive(
            @RequestBody byte[] payload, @RequestHeader HttpHeaders headers) {
        return ResponseEntity.accepted().body(service.receive(payload, headers));
    }

}
