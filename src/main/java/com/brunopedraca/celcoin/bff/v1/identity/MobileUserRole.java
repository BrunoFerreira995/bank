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
@Table(name = "mobile_user_role")
@IdClass(MobileUserRole.Key.class)
class MobileUserRole {
    @Id @Column(name = "user_id") private UUID userId;
    @Id @Enumerated(EnumType.STRING) @Column(name = "role") private MobileRole role;
    protected MobileUserRole() {}
    static final class Key implements Serializable { private UUID userId; private MobileRole role; public Key() {} }
}
