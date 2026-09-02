package com.brunopedraca.celcoin.bff.v1.identity;

import org.springframework.stereotype.Service;

/** Authenticates the separate administrative surface without relying on request ThreadLocals. */
@Service
public class MobileAdministrationAuthorizationService {
    private final MobileSessionService sessions;
    private final MobileUserRoleRepository roles;
    public MobileAdministrationAuthorizationService(MobileSessionService sessions, MobileUserRoleRepository roles) { this.sessions = sessions; this.roles = roles; }
    public void requireAdministrator(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw new MobileUnauthorizedException();
        var userId = sessions.authenticateAccessToken(authorization.substring("Bearer ".length())).userId();
        boolean allowed = roles.findRolesByUserId(userId).stream().anyMatch(role -> role == MobileRole.ADMIN || role == MobileRole.OPERATIONS);
        if (!allowed) throw new MobileForbiddenException();
    }
}
