package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookReceipt;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CelcoinWebhookController {
    private final CelcoinWebhookService service;

    public CelcoinWebhookController(CelcoinWebhookService service) {
        this.service = service;
    }

    @PostMapping("/webhooks/celcoin")
    public ResponseEntity<CelcoinWebhookReceipt> receive(@RequestBody byte[] payload, @RequestHeader HttpHeaders headers) {
        return ResponseEntity.accepted().body(service.receive(payload, headers));
    }

    @GetMapping("/admin/webhooks")
    public List<CelcoinWebhookEventResponse> list() {
        return service.listEvents();
    }

    @PostMapping("/admin/webhooks/{id}/retry")
    public CelcoinWebhookEventResponse retry(@PathVariable UUID id) {
        return service.retry(id);
    }
}
