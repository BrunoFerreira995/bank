package com.brunopedraca.celcoin.bff.v1.onboarding;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mobile_kyc_document")
class MobileKycDocument {
    @Id private UUID id;
    @Column(name = "proposal_id", nullable = false) private String proposalId;
    @Column(nullable = false) private String filename;
    @Column(name = "content_type", nullable = false) private String contentType;
    @Column(nullable = false) private String status;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Lob @Basic(fetch = FetchType.LAZY) @Column(nullable = false) private byte[] content;
    protected MobileKycDocument() {}
    MobileKycDocument(String proposalId, String filename, String contentType, byte[] content) { this.id = UUID.randomUUID(); this.proposalId = proposalId; this.filename = filename; this.contentType = contentType; this.content = content; this.status = "RECEIVED"; this.createdAt = OffsetDateTime.now(); }
    UUID id() { return id; } String filename() { return filename; } String contentType() { return contentType; } String status() { return status; } OffsetDateTime createdAt() { return createdAt; }
}
