package com.brunopedraca.celcoin.bff.v1.identity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/session", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
@Tag(name = "Mobile v1 - Session")
public class MobileSessionController {
    private final MobileSessionService service;
    public MobileSessionController(MobileSessionService service) { this.service = service; }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate a mobile user, or start its MFA challenge")
    public SessionResponse create(@Valid @RequestBody CreateSessionRequest request) { return SessionResponse.from(service.authenticate(request.login(), request.password())); }

    @PostMapping(path = "/mfa", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Validate the current TOTP MFA challenge")
    public SessionResponse verifyMfa(@Valid @RequestBody VerifyMfaRequest request) { return SessionResponse.from(service.verifyMfa(UUID.fromString(request.challengeId()), request.code())); }

    @PostMapping(path = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rotate an unexpired refresh token")
    public SessionResponse refresh(@Valid @RequestBody RefreshSessionRequest request) { return SessionResponse.from(service.refresh(request.refreshToken())); }

    @PostMapping(path = "/step-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elevate the current session with a fresh TOTP code before a risk operation")
    public void stepUp(@RequestHeader("Authorization") String authorization, @Valid @RequestBody StepUpRequest request) {
        service.stepUp(authorization.substring("Bearer ".length()), request.code());
    }

    @PostMapping(path = "/recovery", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Request account recovery without revealing whether the user exists")
    public NeutralResponse recovery(@Valid @RequestBody RecoveryRequest request) { return new NeutralResponse("If the account is eligible, recovery instructions will be sent."); }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke the current session")
    public void delete(@RequestHeader("Authorization") String authorization) { service.revokeCurrentSession(authorization.substring("Bearer ".length())); }


    public record CreateSessionRequest(@NotBlank String login, @NotBlank String password) {}
    public record VerifyMfaRequest(@NotBlank String challengeId, @Pattern(regexp = "\\d{6}") String code) {}
    public record RefreshSessionRequest(@NotBlank String refreshToken) {}
    public record StepUpRequest(@Pattern(regexp = "\\d{6}") String code) {}
    public record RecoveryRequest(@NotBlank String login) {}
    public record NeutralResponse(String message) {}
    public record SessionResponse(String status, String accessToken, String refreshToken, OffsetDateTime expiresAt, String challengeId) {
        static SessionResponse from(MobileSessionService.SessionResult result) { return new SessionResponse(result.status(), result.accessToken(), result.refreshToken(), result.expiresAt(), result.challengeId()); }
    }
}
