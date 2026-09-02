package com.brunopedraca.celcoin.bff.v1.identity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
interface MobileConsentRepository extends JpaRepository<MobileConsent, UUID> {}
