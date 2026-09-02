package com.brunopedraca.celcoin.bff.v1.identity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import com.brunopedraca.celcoin.bff.correlation.CorrelationIdFilter;
import com.brunopedraca.celcoin.bff.v1.common.MobileErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileAuthenticationFilter extends OncePerRequestFilter {
    private final MobileSessionService sessions;
    private final ObjectMapper objectMapper;
    public MobileAuthenticationFilter(MobileSessionService sessions, ObjectMapper objectMapper) { this.sessions = sessions; this.objectMapper = objectMapper; }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/mobile/v1/") || path.equals("/mobile/v1/session") || path.startsWith("/mobile/v1/session/mfa") || path.startsWith("/mobile/v1/session/recovery") || path.startsWith("/mobile/v1/session/refresh") || path.startsWith("/mobile/v1/onboardings");
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) { unauthorized(request, response); return; }
        try {
            MobileSessionService.SessionAuthentication authentication = sessions.authenticateAccessToken(authorization.substring(7));
            MobileAuthentication.set(authentication.userId(), authentication.stepUpAuthenticated());
            chain.doFilter(request, response);
        } catch (MobileUnauthorizedException exception) { unauthorized(request, response); }
        finally { MobileAuthentication.clear(); }
    }
    private void unauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), new MobileErrorResponse("UNAUTHORIZED", "Authentication is required", request.getHeader(CorrelationIdFilter.HEADER), OffsetDateTime.now(), List.of()));
    }
}
