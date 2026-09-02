package com.brunopedraca.celcoin.bff.v1.identity;

public class MobileForbiddenException extends RuntimeException {
    public MobileForbiddenException() { super("You are not allowed to access this resource"); }
}
