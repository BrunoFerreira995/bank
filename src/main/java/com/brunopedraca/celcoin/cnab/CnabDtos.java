package com.brunopedraca.celcoin.cnab;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.Map;

public final class CnabDtos {
    private CnabDtos() {}

    public record CelcoinCnabProcessRequest(
            @NotNull Path file, @NotBlank String clientRequestId, String account) {}

    public record CelcoinCnabProcessResponse(
            String status, String entity, String fileId, String fileType, String clientRequestId,
            Map<String, Object> error, Map<String, Object> raw) {}

    public record CelcoinCnabStatusResponse(
            String status, String entity, String fileId, String clientRequestId,
            Map<String, Object> error, Map<String, Object> raw) {}
}
