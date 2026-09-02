package com.brunopedraca.celcoin.bff.v1.identity;

public class MobileStepUpRequiredException extends RuntimeException {
    public MobileStepUpRequiredException() { super("Step-up authentication is required"); }
}
