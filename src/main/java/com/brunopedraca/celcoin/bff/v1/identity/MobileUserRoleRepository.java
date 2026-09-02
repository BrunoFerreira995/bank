package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface MobileUserRoleRepository extends JpaRepository<MobileUserRole, MobileUserRole.Key> {
    @Query("select role.role from MobileUserRole role where role.userId = :userId")
    List<MobileRole> findRolesByUserId(UUID userId);
}
