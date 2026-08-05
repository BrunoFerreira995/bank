package com.brunopedraca.celcoin.common.idempotency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CelcoinIdempotencyRecordRepository extends JpaRepository<CelcoinIdempotencyRecord, UUID> {
    Optional<CelcoinIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    List<CelcoinIdempotencyRecord> findByOperation(String operation);

    List<CelcoinIdempotencyRecord> findByOperationAndStatus(String operation, String status);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
