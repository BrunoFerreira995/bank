package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_user")
class MobileUser {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "mfa_secret")
    private String mfaSecret;
    @Column(nullable = false)
    private boolean active;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "active_account_id")
    private String activeAccountId;

    protected MobileUser() {}

    UUID id() { return id; }
    String login() { return login; }
    String passwordHash() { return passwordHash; }
    String mfaSecret() { return mfaSecret; }
    boolean active() { return active; }
    String activeAccountId() { return activeAccountId; }
    void setActiveAccountId(String accountId) { activeAccountId = accountId; }
    void changePassword(String hash) { passwordHash = hash; }
}
