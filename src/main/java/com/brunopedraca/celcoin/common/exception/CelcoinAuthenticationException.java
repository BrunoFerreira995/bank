package com.brunopedraca.celcoin.common.exception;

import org.springframework.http.HttpStatusCode;

public class CelcoinAuthenticationException extends CelcoinException {
    public CelcoinAuthenticationException(String message) {
        super(message);
    }

    public CelcoinAuthenticationException(String message, HttpStatusCode status, Throwable cause) {
        super(message, status, null, null, null, cause);
    }
}
