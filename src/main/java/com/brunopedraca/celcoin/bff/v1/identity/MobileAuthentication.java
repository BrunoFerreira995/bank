package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.UUID;

public final class MobileAuthentication {
    private static final ThreadLocal<Principal> CURRENT_USER = new ThreadLocal<>();
    private MobileAuthentication() {}
    public static void set(UUID userId, boolean stepUpAuthenticated) { CURRENT_USER.set(new Principal(userId, stepUpAuthenticated)); }
    public static UUID requiredUserId() {
        Principal principal = CURRENT_USER.get();
        if (principal == null) throw new MobileUnauthorizedException();
        return principal.userId();
    }
    public static void requireStepUp() { if (CURRENT_USER.get() == null || !CURRENT_USER.get().stepUpAuthenticated()) throw new MobileStepUpRequiredException(); }
    public static void clear() { CURRENT_USER.remove(); }
    private record Principal(UUID userId, boolean stepUpAuthenticated) {}
}
