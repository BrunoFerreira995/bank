package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_session")
class MobileSession {
    @Id
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "access_token_hash", nullable = false, unique = true)
    private String accessTokenHash;
    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    @Column(name = "refresh_expires_at", nullable = false)
    private OffsetDateTime refreshExpiresAt;
    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "mfa_verified_at")
    private OffsetDateTime mfaVerifiedAt;

    protected MobileSession() {}

    private MobileSession(UUID userId, String accessTokenHash, String refreshTokenHash, OffsetDateTime expiresAt, OffsetDateTime refreshExpiresAt, boolean mfaVerified) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.accessTokenHash = accessTokenHash;
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
        this.refreshExpiresAt = refreshExpiresAt;
        this.createdAt = OffsetDateTime.now();
        this.mfaVerifiedAt = mfaVerified ? this.createdAt : null;
    }

    static MobileSession create(UUID userId, String accessHash, String refreshHash, OffsetDateTime expiresAt, OffsetDateTime refreshExpiresAt, boolean mfaVerified) {
        return new MobileSession(userId, accessHash, refreshHash, expiresAt, refreshExpiresAt, mfaVerified);
    }

    UUID userId() { return userId; }
    String refreshTokenHash() { return refreshTokenHash; }
    boolean accessActive(OffsetDateTime now) { return revokedAt == null && expiresAt.isAfter(now); }
    boolean refreshActive(OffsetDateTime now) { return revokedAt == null && refreshExpiresAt.isAfter(now); }
    void revoke() { revokedAt = OffsetDateTime.now(); }
    boolean stepUpActive(OffsetDateTime now) { return mfaVerifiedAt != null && mfaVerifiedAt.plusMinutes(5).isAfter(now); }
    void markStepUp() { mfaVerifiedAt = OffsetDateTime.now(); }
}
