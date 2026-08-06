package com.brunopedraca.celcoin.pixauto;

import java.util.EnumSet;
import java.util.Locale;

/** Official consent and recurring-payment transitions for Pix Automático. */
public final class PixAutoStateMachine {
    private PixAutoStateMachine() {}
    public enum ConsentState { AWAITING_AUTHORISATION, PARTIALLY_ACCEPTED, AUTHORISED, REJECTED, REVOKED, CONSUMED }
    public enum PaymentState { RCVD, ACCP, ACPD, SCHD, PDNG, ACSC, RJCT, CANC }

    public static boolean canTransition(ConsentState from, ConsentState to) {
        return switch (from) {
            case AWAITING_AUTHORISATION -> EnumSet.of(ConsentState.PARTIALLY_ACCEPTED, ConsentState.AUTHORISED, ConsentState.REJECTED).contains(to);
            case PARTIALLY_ACCEPTED -> EnumSet.of(ConsentState.AUTHORISED, ConsentState.REJECTED).contains(to);
            case AUTHORISED -> EnumSet.of(ConsentState.REVOKED, ConsentState.CONSUMED).contains(to);
            case REJECTED, REVOKED, CONSUMED -> false;
        };
    }
    public static boolean canTransition(PaymentState from, PaymentState to) {
        return switch (from) {
            case RCVD -> EnumSet.of(PaymentState.ACCP, PaymentState.PDNG, PaymentState.RJCT, PaymentState.CANC).contains(to);
            case ACCP -> EnumSet.of(PaymentState.ACPD, PaymentState.SCHD, PaymentState.RJCT).contains(to);
            case ACPD -> EnumSet.of(PaymentState.ACSC, PaymentState.RJCT).contains(to);
            case SCHD -> EnumSet.of(PaymentState.ACPD, PaymentState.RJCT, PaymentState.CANC).contains(to);
            case PDNG -> EnumSet.of(PaymentState.ACCP, PaymentState.RJCT).contains(to);
            case ACSC, RJCT, CANC -> false;
        };
    }
    public static ConsentState consent(String status) { return parse(status, ConsentState.class); }
    public static PaymentState payment(String status) { return parse(status, PaymentState.class); }
    private static <T extends Enum<T>> T parse(String value, Class<T> type) {
        if (value == null) throw new IllegalArgumentException("status is required");
        try { return Enum.valueOf(type, value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Unknown status: " + value, e); }
    }
}
