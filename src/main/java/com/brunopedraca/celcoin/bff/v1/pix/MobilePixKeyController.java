package com.brunopedraca.celcoin.bff.v1.pix;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixDeleteKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixUpdateKeyRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/mobile/v1/pix/keys", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobilePixKeyController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobilePixKeyController(CelcoinClient client, MobileAccountAuthorizationService authorization) { this.client = client; this.authorization = authorization; }
    @GetMapping public Object list(@RequestParam String accountId) { authorization.requireRead(accountId); return client.pix().listKeys(accountId).listKeys(); }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) public Object create(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody KeyRequest request) { authorization.requireRisk(request.accountId()); return client.pix().createKey(new CelcoinPixKeyRequest(request.accountId(), request.keyType(), request.key()), key); }
    @DeleteMapping("/{pixKey}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable String pixKey, @RequestParam String accountId, @RequestHeader("Idempotency-Key") @NotBlank String key) { authorization.requireRisk(accountId); client.pix().deleteKey(new CelcoinPixDeleteKeyRequest(accountId, pixKey), key); }
    @PatchMapping("/{pixKey}") public Object update(@PathVariable String pixKey, @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, @Valid @RequestBody UpdateKeyRequest request) { authorization.requireRisk(request.accountId()); return client.pix().updateKeyName(new CelcoinPixUpdateKeyRequest(request.accountId(), pixKey, request.name(), request.tradeName()), idempotencyKey); }
    public record KeyRequest(@NotBlank String accountId, @NotBlank String keyType, String key) {}
    public record UpdateKeyRequest(@NotBlank String accountId, String name, String tradeName) {}
}
