package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "mobile_account_grant")
@IdClass(MobileAccountGrant.Key.class)
class MobileAccountGrant {
    @Id @Column(name = "user_id") private UUID userId;
    @Id @Column(name = "account_id") private String accountId;
    @Id @Enumerated(EnumType.STRING) @Column(name = "permission") private AccountPermission permission;
    protected MobileAccountGrant() {}
    static final class Key implements Serializable { private UUID userId; private String accountId; private AccountPermission permission; public Key() {} }
}
