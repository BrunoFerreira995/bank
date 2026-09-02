package com.brunopedraca.celcoin.bff.audit;

import org.springframework.stereotype.Service;

@Service
public class MobileAuditService {
    private final MobileRequestAuditRepository repository;

    public MobileAuditService(MobileRequestAuditRepository repository) {
        this.repository = repository;
    }

    public void record(String method, String path, int statusCode, String correlationId, long durationMs) {
        repository.save(MobileRequestAudit.of(method, path, statusCode, correlationId, durationMs));
    }
}
