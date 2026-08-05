package com.brunopedraca.celcoin.common.idempotency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.common.exception.CelcoinConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CelcoinIdempotencyServiceTest {
    private CelcoinIdempotencyRecordRepository repository;
    private CelcoinIdempotencyService service;

    @BeforeEach
    void setUp() {
        repository = mock(CelcoinIdempotencyRecordRepository.class);
        service = new CelcoinIdempotencyService(repository, new ObjectMapper());
    }

    @Test
    void beginsBySavingStartedRecordWhenKeyIsNew() {
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());

        Optional<String> replayed = service.begin("key-1", "/pix/cashout", request("10"));

        assertThat(replayed).isEmpty();
        verify(repository).save(any(CelcoinIdempotencyRecord.class));
    }

    @Test
    void replaysCompletedResponseWhenRequestMatches() {
        CelcoinIdempotencyRecord completed =
                new CelcoinIdempotencyRecord("key-1", "/pix/cashout", service.hash(request("10")));
        completed.complete("resp-hash", "{\"id\":\"tx-1\"}");
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(completed));

        Optional<String> replayed = service.begin("key-1", "/pix/cashout", request("10"));

        assertThat(replayed).contains("{\"id\":\"tx-1\"}");
    }

    @Test
    void doesNotReplayWhenRecordIsStillStarted() {
        CelcoinIdempotencyRecord started =
                new CelcoinIdempotencyRecord("key-1", "/pix/cashout", service.hash(request("10")));
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(started));

        Optional<String> replayed = service.begin("key-1", "/pix/cashout", request("10"));

        assertThat(replayed).isEmpty();
    }

    @Test
    void rejectsKeyReusedWithDifferentOperation() {
        CelcoinIdempotencyRecord record =
                new CelcoinIdempotencyRecord("key-1", "/pix/cashin", service.hash(request("10")));
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> service.begin("key-1", "/pix/cashout", request("10")))
                .isInstanceOf(CelcoinConflictException.class)
                .hasMessageContaining("reused with a different request");
    }

    @Test
    void rejectsKeyReusedWithDifferentRequestHash() {
        CelcoinIdempotencyRecord record =
                new CelcoinIdempotencyRecord("key-1", "/pix/cashout", service.hash(request("10")));
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> service.begin("key-1", "/pix/cashout", request("20")))
                .isInstanceOf(CelcoinConflictException.class);
    }

    @Test
    void completesRecordWithResponseBody() {
        CelcoinIdempotencyRecord record = new CelcoinIdempotencyRecord("key-1", "/pix/cashout", "hash");
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(record));

        service.complete("key-1", new SimpleResponse("tx-1"));

        assertThat(record.getStatus()).isEqualTo(CelcoinIdempotencyRecord.STATUS_COMPLETED);
        assertThat(record.getResponseBody()).isEqualTo("{\"id\":\"tx-1\"}");
        assertThat(record.getResponseHash()).isNotBlank();
        assertThat(record.getCompletedAt()).isNotNull();
    }

    @Test
    void failsRecordWithError() {
        CelcoinIdempotencyRecord record = new CelcoinIdempotencyRecord("key-1", "/pix/cashout", "hash");
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(record));

        service.fail("key-1", "boom");

        assertThat(record.getStatus()).isEqualTo(CelcoinIdempotencyRecord.STATUS_FAILED);
        assertThat(record.getLastError()).isEqualTo("boom");
    }

    @Test
    void queriesByOperationAndStatus() {
        CelcoinIdempotencyRecord record = new CelcoinIdempotencyRecord("key-1", "/pix/cashout", "hash");
        when(repository.findByOperation("/pix/cashout")).thenReturn(List.of(record));
        when(repository.findByOperationAndStatus("/pix/cashout", CelcoinIdempotencyRecord.STATUS_COMPLETED))
                .thenReturn(List.of(record));
        when(repository.existsByIdempotencyKey("key-1")).thenReturn(true);
        when(repository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(record));

        assertThat(service.findByOperation("/pix/cashout")).containsExactly(record);
        assertThat(service.findByOperationAndStatus("/pix/cashout", CelcoinIdempotencyRecord.STATUS_COMPLETED))
                .containsExactly(record);
        assertThat(service.exists("key-1")).isTrue();
        assertThat(service.findByIdempotencyKey("key-1")).contains(record);
    }

    @Test
    void hashesDeterministicallyAndDeserializes() {
        String hashOne = service.hash(request("10"));
        String hashTwo = service.hash(request("10"));

        assertThat(hashOne).isEqualTo(hashTwo).hasSize(64);
        assertThat(service.deserialize("{\"id\":\"tx-1\"}", SimpleResponse.class)
                        .id())
                .isEqualTo("tx-1");
    }

    @Test
    void handlesConcurrentInsertOfSameKey() {
        CelcoinIdempotencyRecord record =
                new CelcoinIdempotencyRecord("key-1", "/pix/cashout", service.hash(request("10")));
        record.complete("rh", "{\"id\":\"tx-1\"}");
        when(repository.findByIdempotencyKey("key-1"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(record));
        org.springframework.dao.DataIntegrityViolationException violation =
                new org.springframework.dao.DataIntegrityViolationException("duplicate key");
        org.mockito.Mockito.doThrow(violation).when(repository).save(any(CelcoinIdempotencyRecord.class));

        Optional<String> replayed = service.begin("key-1", "/pix/cashout", request("10"));

        assertThat(replayed).contains("{\"id\":\"tx-1\"}");
    }

    @Test
    void failsWhenDeserializingInvalidReplay() {
        assertThatThrownBy(() -> service.deserialize("{invalid", SimpleResponse.class))
                .isInstanceOf(com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException.class);
    }

    @Test
    void hashesNullAsEmptyPayload() {
        String hash = service.hash(null);
        String hashAgain = service.hash(null);

        assertThat(hash).hasSize(64).isEqualTo(hashAgain);
    }

    @Test
    void recordExposesLifecycleValues() {
        CelcoinIdempotencyRecord record = new CelcoinIdempotencyRecord("key-1", "/pix/cashout", "hash");

        assertThat(record.getId()).isNotNull();
        assertThat(record.getIdempotencyKey()).isEqualTo("key-1");
        assertThat(record.getOperation()).isEqualTo("/pix/cashout");
        assertThat(record.getRequestHash()).isEqualTo("hash");
        assertThat(record.getCreatedAt()).isNotNull();
        assertThat(record.getStatus()).isEqualTo(CelcoinIdempotencyRecord.STATUS_STARTED);
    }

    private static Request request(String amount) {
        return new Request(amount);
    }

    record Request(String amount) {}

    record SimpleResponse(String id) {}
}
