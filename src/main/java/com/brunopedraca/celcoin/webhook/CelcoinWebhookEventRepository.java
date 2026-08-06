package com.brunopedraca.celcoin.webhook;

import java.util.Optional;
import java.util.UUID;
import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CelcoinWebhookEventRepository extends JpaRepository<CelcoinWebhookEvent, UUID> {
    Optional<CelcoinWebhookEvent> findByExternalEventId(String externalEventId);

    int deleteByReceivedAtBefore(OffsetDateTime cutoff);
}
