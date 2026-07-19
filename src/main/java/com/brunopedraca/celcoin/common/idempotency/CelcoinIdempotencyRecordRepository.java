package com.brunopedraca.celcoin.common.idempotency;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CelcoinIdempotencyRecordRepository extends JpaRepository<CelcoinIdempotencyRecord, UUID> {
    Optional<CelcoinIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
