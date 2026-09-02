package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_mfa_challenge")
class MobileMfaChallenge {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "consumed_at")
    private OffsetDateTime consumedAt;
    @Column(nullable = false)
    private int attempts;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected MobileMfaChallenge() {}

    MobileMfaChallenge(UUID userId, OffsetDateTime expiresAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = OffsetDateTime.now();
    }

    UUID id() { return id; }
    UUID userId() { return userId; }
    boolean usable(OffsetDateTime now) { return consumedAt == null && expiresAt.isAfter(now) && attempts < 5; }
    void failedAttempt() { attempts++; }
    void consume() { consumedAt = OffsetDateTime.now(); }
}
