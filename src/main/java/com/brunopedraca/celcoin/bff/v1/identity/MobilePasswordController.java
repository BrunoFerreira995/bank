package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/password", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobilePasswordController {
    private final MobileSessionService sessions;
    public MobilePasswordController(MobileSessionService sessions) { this.sessions = sessions; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void change(@Valid @RequestBody ChangePasswordRequest request) { sessions.changePassword(request.currentPassword(), request.newPassword()); }
    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank @Size(min = 8, max = 256) String newPassword) {}
}
