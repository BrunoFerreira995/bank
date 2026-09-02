package com.brunopedraca.celcoin.bff.v1.identity;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileProfileController {
    private final MobileSessionService sessions;
    public MobileProfileController(MobileSessionService sessions) { this.sessions = sessions; }
    @GetMapping public MobileSessionService.Profile get() { return sessions.profile(); }
}
