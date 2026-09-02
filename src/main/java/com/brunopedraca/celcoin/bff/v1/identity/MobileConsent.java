package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_consent")
class MobileConsent {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String version;
    @Column(name = "accepted_at", nullable = false) private OffsetDateTime acceptedAt;
    protected MobileConsent() {}
    MobileConsent(UUID userId, String type, String version) { this.id = UUID.randomUUID(); this.userId = userId; this.type = type; this.version = version; this.acceptedAt = OffsetDateTime.now(); }
    UUID id() { return id; } String type() { return type; } String version() { return version; } OffsetDateTime acceptedAt() { return acceptedAt; }
}
