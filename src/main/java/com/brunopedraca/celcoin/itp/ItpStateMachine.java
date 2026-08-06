package com.brunopedraca.celcoin.itp;

import java.util.EnumSet;
import java.util.Locale;

public final class ItpStateMachine {
    private ItpStateMachine() {}
    public enum ConsentState { AWAITING_AUTHORISATION, AUTHORISED, REJECTED, CONSUMED, EXPIRED }
    public enum PaymentState { PDNG, ACSP, ACSC, RJCT }

    public static boolean canTransition(ConsentState from, ConsentState to) {
        return switch (from) {
            case AWAITING_AUTHORISATION -> EnumSet.of(ConsentState.AUTHORISED, ConsentState.REJECTED, ConsentState.EXPIRED).contains(to);
            case AUTHORISED -> EnumSet.of(ConsentState.CONSUMED, ConsentState.EXPIRED).contains(to);
            case REJECTED, CONSUMED, EXPIRED -> false;
        };
    }
    public static boolean canTransition(PaymentState from, PaymentState to) {
        return switch (from) {
            case PDNG -> EnumSet.of(PaymentState.ACSP, PaymentState.RJCT).contains(to);
            case ACSP -> EnumSet.of(PaymentState.ACSC, PaymentState.RJCT).contains(to);
            case ACSC, RJCT -> false;
        };
    }
    public static ConsentState consent(String value) { return parse(value, ConsentState.class); }
    public static PaymentState payment(String value) { return parse(value, PaymentState.class); }
    private static <T extends Enum<T>> T parse(String value, Class<T> type) {
        if (value == null) throw new IllegalArgumentException("status is required");
        try { return Enum.valueOf(type, value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Unknown status: " + value, e); }
    }
}
