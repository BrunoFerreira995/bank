package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface MobileUserAccountRepository extends JpaRepository<MobileUserAccount, MobileUserAccount.Key> {
    boolean existsByUserIdAndAccountId(UUID userId, String accountId);
    List<MobileUserAccount> findByUserId(UUID userId);
}
