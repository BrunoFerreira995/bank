package com.brunopedraca.celcoin.common.idempotency;

import com.brunopedraca.celcoin.common.exception.CelcoinConflictException;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages idempotency records per operation. An idempotency key is scoped to a
 * single operation (endpoint) and request hash. Reusing a key with a different
 * operation or request hash is rejected with {@link CelcoinConflictException}.
 */
public class CelcoinIdempotencyService {
    private static final String SHA_256 = "SHA-256";

    private final CelcoinIdempotencyRecordRepository repository;
    private final ObjectMapper objectMapper;

    public CelcoinIdempotencyService(CelcoinIdempotencyRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Marks the operation as started unless a completed record already exists.
     *
     * @return the stored response body when the request was already completed, empty otherwise
     */
    public Optional<String> begin(String idempotencyKey, String operation, Object request) {
        String requestHash = hash(request);
        Optional<CelcoinIdempotencyRecord> existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return validateAndReplay(existing.get(), operation, requestHash);
        }
        try {
            repository.save(new CelcoinIdempotencyRecord(idempotencyKey, operation, requestHash));
            return Optional.empty();
        } catch (DataIntegrityViolationException e) {
            CelcoinIdempotencyRecord record = repository
                    .findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() ->
                            new CelcoinConflictException("Idempotency key " + idempotencyKey + " is already in use"));
            return validateAndReplay(record, operation, requestHash);
        }
    }

    @Transactional
    public void complete(String idempotencyKey, Object response) {
        repository
                .findByIdempotencyKey(idempotencyKey)
                .ifPresent(record -> record.complete(hash(response), toJson(response)));
    }

    @Transactional
    public void fail(String idempotencyKey, String error) {
        repository.findByIdempotencyKey(idempotencyKey).ifPresent(record -> record.fail(error));
    }

    public List<CelcoinIdempotencyRecord> findByOperation(String operation) {
        return repository.findByOperation(operation);
    }

    public List<CelcoinIdempotencyRecord> findByOperationAndStatus(String operation, String status) {
        return repository.findByOperationAndStatus(operation, status);
    }

    public Optional<CelcoinIdempotencyRecord> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey);
    }

    public boolean exists(String idempotencyKey) {
        return repository.existsByIdempotencyKey(idempotencyKey);
    }

    public <T> T deserialize(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (Exception e) {
            throw new CelcoinIntegrationException("Unable to replay cached idempotent response", e);
        }
    }

    public String hash(Object request) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            return HexFormat.of().formatHex(digest.digest(toJson(request).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private Optional<String> validateAndReplay(CelcoinIdempotencyRecord record, String operation, String requestHash) {
        if (!record.getOperation().equals(operation) || !record.getRequestHash().equals(requestHash)) {
            throw new CelcoinConflictException(
                    "Idempotency key " + record.getIdempotencyKey() + " was reused with a different request");
        }
        if (CelcoinIdempotencyRecord.STATUS_COMPLETED.equals(record.getStatus()) && record.getResponseBody() != null) {
            return Optional.of(record.getResponseBody());
        }
        return Optional.empty();
    }

    private String toJson(Object value) {
        try {
            if (value == null) {
                return "";
            }
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
