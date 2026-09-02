package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MobileMfaChallengeRepository extends JpaRepository<MobileMfaChallenge, UUID> {
    Optional<MobileMfaChallenge> findById(UUID id);
}
