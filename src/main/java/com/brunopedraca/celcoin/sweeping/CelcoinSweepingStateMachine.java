package com.brunopedraca.celcoin.sweeping;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/** Validates the consent and Pix payment transitions documented by Celcoin. */
public final class CelcoinSweepingStateMachine {
    private CelcoinSweepingStateMachine() {}

    public enum ConsentState {
        AWAITING_AUTHORISATION, AUTHORISED, REJECTED, CONSUMED, EXPIRED, REVOKED
    }

    public enum PaymentState {
        PDNG, SCHD, ACSP, ACSC, RJCT, CANC
    }

    public static boolean canTransition(ConsentState from, ConsentState to) {
        return switch (from) {
            case AWAITING_AUTHORISATION -> EnumSet.of(
                    ConsentState.AUTHORISED, ConsentState.REJECTED, ConsentState.REVOKED).contains(to);
            case AUTHORISED -> EnumSet.of(
                    ConsentState.CONSUMED, ConsentState.EXPIRED, ConsentState.REVOKED).contains(to);
            case REJECTED, CONSUMED, EXPIRED, REVOKED -> false;
        };
    }

    public static boolean canTransition(PaymentState from, PaymentState to) {
        return switch (from) {
            case PDNG -> EnumSet.of(PaymentState.SCHD, PaymentState.ACSP, PaymentState.RJCT).contains(to);
            case SCHD -> EnumSet.of(PaymentState.ACSP, PaymentState.CANC, PaymentState.RJCT).contains(to);
            case ACSP -> EnumSet.of(PaymentState.ACSC, PaymentState.RJCT).contains(to);
            case ACSC, RJCT, CANC -> false;
        };
    }

    public static ConsentState consent(String status) {
        return parse(status, ConsentState.class);
    }

    public static PaymentState payment(String status) {
        return parse(status, PaymentState.class);
    }

    public static boolean isTerminal(ConsentState state) {
        return Set.of(ConsentState.REJECTED, ConsentState.CONSUMED,
                ConsentState.EXPIRED, ConsentState.REVOKED).contains(state);
    }

    public static boolean isTerminal(PaymentState state) {
        return Set.of(PaymentState.ACSC, PaymentState.RJCT, PaymentState.CANC).contains(state);
    }

    private static <T extends Enum<T>> T parse(String status, Class<T> type) {
        if (status == null) throw new IllegalArgumentException("status is required");
        try {
            return Enum.valueOf(type, status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown " + type.getSimpleName() + ": " + status, exception);
        }
    }
}
