package com.brunopedraca.celcoin.bff.v1.onboarding;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
interface MobileKycDocumentRepository extends JpaRepository<MobileKycDocument, UUID> { List<MobileKycDocument> findByProposalIdOrderByCreatedAtDesc(String proposalId); }
