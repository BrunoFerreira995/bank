package com.brunopedraca.celcoin.bff.audit;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface MobileRequestAuditRepository extends JpaRepository<MobileRequestAudit, UUID> {}
