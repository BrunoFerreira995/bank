package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/mobile/v1/consents", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileConsentController {
    private final MobileConsentRepository consents;
    public MobileConsentController(MobileConsentRepository consents) { this.consents = consents; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional
    public ConsentResponse accept(@Valid @RequestBody ConsentRequest request) {
        MobileConsent consent = consents.save(new MobileConsent(MobileAuthentication.requiredUserId(), request.type(), request.version()));
        return new ConsentResponse(consent.id().toString(), consent.type(), consent.version(), consent.acceptedAt());
    }
    public record ConsentRequest(@NotBlank String type, @NotBlank String version) {}
    public record ConsentResponse(String id, String type, String version, OffsetDateTime acceptedAt) {}
}
