package com.brunopedraca.celcoin.common.exception;

public class CelcoinIntegrationException extends CelcoinException {
    public CelcoinIntegrationException(String message) {
        super(message);
    }

    public CelcoinIntegrationException(String message, Throwable cause) {
        super(message, null, null, null, null, cause);
    }
}
