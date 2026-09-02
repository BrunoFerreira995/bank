package com.brunopedraca.celcoin.bff.correlation;

import com.brunopedraca.celcoin.bff.audit.MobileAuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class CorrelationIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Correlation-Id";
    private final MobileAuditService auditService;

    public CorrelationIdFilter(MobileAuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/mobile/v1/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String correlationId = validOrNew(request.getHeader(HEADER));
        long startedAt = System.nanoTime();
        response.setHeader(HEADER, correlationId);
        MDC.put("correlationId", correlationId);
        try {
            chain.doFilter(request, response);
        } finally {
            try {
                auditService.record(
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        correlationId,
                        (System.nanoTime() - startedAt) / 1_000_000);
            } finally {
                MDC.remove("correlationId");
            }
        }
    }

    private static String validOrNew(String candidate) {
        return candidate != null && candidate.matches("[A-Za-z0-9._-]{1,120}")
                ? candidate
                : UUID.randomUUID().toString();
    }
}
