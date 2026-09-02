package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MobileAccountGrantRepository extends JpaRepository<MobileAccountGrant, MobileAccountGrant.Key> {
    boolean existsByUserIdAndAccountIdAndPermission(UUID userId, String accountId, AccountPermission permission);
}
