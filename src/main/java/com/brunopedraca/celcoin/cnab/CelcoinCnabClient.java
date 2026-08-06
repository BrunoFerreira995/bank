package com.brunopedraca.celcoin.cnab;

import com.brunopedraca.celcoin.cnab.CnabDtos.*;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinRequestContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

public final class CelcoinCnabClient implements CelcoinCnabOperations {
    private static final String BASE = "/baas/v2/cnab-file";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final CelcoinHttpClient httpClient;

    public CelcoinCnabClient(CelcoinHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CelcoinCnabProcessResponse process(CelcoinCnabProcessRequest request, String idempotencyKey) {
        ensureConfigured();
        Path file = request.file().toAbsolutePath().normalize();
        try {
            if (!Files.isRegularFile(file)) throw new IllegalArgumentException("CNAB file does not exist");
            if (Files.size(file) > MAX_FILE_SIZE) throw new IllegalArgumentException("CNAB file exceeds 10 MB");
            if (!StringUtils.hasText(request.clientRequestId())) {
                throw new IllegalArgumentException("clientRequestId is required for CNAB processing");
            }
            return httpClient.postMultipart(BASE, new FileSystemResource(file), request.clientRequestId(),
                    request.account(), CelcoinCnabProcessResponse.class, CelcoinRequestContext.create(idempotencyKey));
        } catch (IOException exception) {
            throw new CelcoinIntegrationException("Unable to inspect CNAB file", exception);
        }
    }

    @Override
    public CelcoinCnabStatusResponse getStatus(String fileIdOrClientRequestId) {
        ensureConfigured();
        return httpClient.get(BASE + "/" + encode(fileIdOrClientRequestId),
                CelcoinCnabStatusResponse.class, CelcoinRequestContext.create(null));
    }

    @Override
    public byte[] downloadInput(String fileIdOrClientRequestId) {
        ensureConfigured();
        return httpClient.download(BASE + "/" + encode(fileIdOrClientRequestId) + "/fileinput",
                CelcoinRequestContext.create(null));
    }

    @Override
    public byte[] downloadOutput(String fileIdOrClientRequestId) {
        ensureConfigured();
        return httpClient.download(BASE + "/" + encode(fileIdOrClientRequestId) + "/fileoutput",
                CelcoinRequestContext.create(null));
    }

    private void ensureConfigured() {
        if (httpClient == null) throw new CelcoinIntegrationException(
                "Celcoin CNAB endpoint path is not configured because the official contract was not provided");
    }

    private static String encode(String value) {
        return StringUtils.hasText(value)
                ? java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                : "";
    }
}
