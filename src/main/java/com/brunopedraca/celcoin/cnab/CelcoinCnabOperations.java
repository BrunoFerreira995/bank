package com.brunopedraca.celcoin.cnab;

import com.brunopedraca.celcoin.cnab.CnabDtos.*;

public interface CelcoinCnabOperations {
    CelcoinCnabProcessResponse process(CelcoinCnabProcessRequest request, String idempotencyKey);

    CelcoinCnabStatusResponse getStatus(String fileIdOrClientRequestId);

    byte[] downloadInput(String fileIdOrClientRequestId);

    byte[] downloadOutput(String fileIdOrClientRequestId);
}
