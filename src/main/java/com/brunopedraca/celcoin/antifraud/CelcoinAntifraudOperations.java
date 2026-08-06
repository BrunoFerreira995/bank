package com.brunopedraca.celcoin.antifraud;

import com.brunopedraca.celcoin.antifraud.CelcoinAntifraudDtos.RiskAssessment;

public interface CelcoinAntifraudOperations {
    RiskAssessment assess(String taxId);

    RiskAssessment assess(String taxId, boolean verbose);
}
