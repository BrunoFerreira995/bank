package com.brunopedraca.celcoin.bff.v1.onboarding;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycBusinessAccountRequest;
import com.brunopedraca.celcoin.onboarding.OnboardingDtos.CelcoinKycPersonAccountRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/** Public onboarding surface intentionally accepts only the two KYC proposal shapes. */
@RestController
@RequestMapping(path = "/mobile/v1/onboardings", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileOnboardingController {
    private final CelcoinClient client;
    private final MobileKycDocumentRepository documents;
    public MobileOnboardingController(CelcoinClient client, MobileKycDocumentRepository documents) { this.client = client; this.documents = documents; }
    @PostMapping(path = "/pf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object createPf(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody CelcoinKycPersonAccountRequest request) { return client.onboarding().createPersonAccount(request, key); }
    @PostMapping(path = "/pj", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object createPj(@RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody CelcoinKycBusinessAccountRequest request) { return client.onboarding().createBusinessAccount(request, key); }
    @GetMapping("/{proposalId}")
    public Object status(@PathVariable String proposalId) { return client.onboarding().getProposal(proposalId); }
    @PostMapping(path = "/{proposalId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public KycDocumentResponse upload(@PathVariable String proposalId, @RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > 10_000_000L) throw new IllegalArgumentException("Document must be between 1 byte and 10 MB");
        String filename = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        MobileKycDocument document = documents.save(new MobileKycDocument(proposalId, filename, file.getContentType() == null ? "application/octet-stream" : file.getContentType(), file.getBytes()));
        return KycDocumentResponse.from(document);
    }
    @GetMapping("/{proposalId}/documents")
    public java.util.List<KycDocumentResponse> documents(@PathVariable String proposalId) { return documents.findByProposalIdOrderByCreatedAtDesc(proposalId).stream().map(KycDocumentResponse::from).toList(); }
    public record KycDocumentResponse(String id, String filename, String contentType, String status, java.time.OffsetDateTime createdAt) { static KycDocumentResponse from(MobileKycDocument value) { return new KycDocumentResponse(value.id().toString(), value.filename(), value.contentType(), value.status(), value.createdAt()); } }
}
