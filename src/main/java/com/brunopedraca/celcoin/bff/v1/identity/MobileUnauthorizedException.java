package com.brunopedraca.celcoin.bff.v1.identity;

public class MobileUnauthorizedException extends RuntimeException {
    public MobileUnauthorizedException() { super("Authentication is required"); }
}
