package com.brunopedraca.celcoin.common.exception;

public class CelcoinTimeoutException extends CelcoinException {
    public CelcoinTimeoutException(String message, Throwable cause) {
        super(message, null, null, null, null, cause);
    }
}
