package com.brunopedraca.celcoin.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class CelcoinTokenServiceTest {
    private final Instant now = Instant.parse("2026-07-19T12:00:00Z");

    @Test
    void reusesCachedTokenBeforeRefreshMargin() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken()).thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 3600, null, now));

        CelcoinTokenService service =
                new CelcoinTokenService(tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
        assertThat(service.getAccessToken()).isEqualTo("token-1");
        verify(tokenClient, times(1)).generateToken();
    }

    @Test
    void refreshesTokenInsideRefreshMargin() {
        CelcoinTokenClient tokenClient = mock(CelcoinTokenClient.class);
        when(tokenClient.generateToken())
                .thenReturn(new CelcoinTokenResponse("token-1", "Bearer", 30, null, now))
                .thenReturn(new CelcoinTokenResponse("token-2", "Bearer", 3600, null, now));

        CelcoinTokenService service =
                new CelcoinTokenService(tokenClient, TestProperties.celcoin("http://localhost"), Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.getAccessToken()).isEqualTo("token-1");
        assertThat(service.getAccessToken()).isEqualTo("token-2");
        verify(tokenClient, times(2)).generateToken();
    }
}
