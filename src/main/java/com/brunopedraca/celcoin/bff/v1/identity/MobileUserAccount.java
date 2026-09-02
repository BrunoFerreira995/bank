package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "mobile_user_account")
@IdClass(MobileUserAccount.Key.class)
class MobileUserAccount {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Id
    @Column(name = "account_id")
    private String accountId;

    protected MobileUserAccount() {}

    String accountId() { return accountId; }

    static final class Key implements Serializable {
        private UUID userId;
        private String accountId;
        public Key() {}
    }
}
