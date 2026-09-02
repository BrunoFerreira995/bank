package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MobileSessionRepository extends JpaRepository<MobileSession, UUID> {
    Optional<MobileSession> findByAccessTokenHash(String accessTokenHash);
    Optional<MobileSession> findByRefreshTokenHash(String refreshTokenHash);
}
