package com.brunopedraca.celcoin.bff;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Explicit opt-in for product surfaces that may not be contracted in every tenant. */
@ConfigurationProperties(prefix = "mobile.bff.features")
public record MobileBffFeatureProperties(boolean cards, boolean credit, boolean escrow) {}
