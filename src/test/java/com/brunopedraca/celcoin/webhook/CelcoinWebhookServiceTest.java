package com.brunopedraca.celcoin.webhook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;

class CelcoinWebhookServiceTest {
    @Test
    void storesEventAndMarksDuplicateByExternalId() {
        CelcoinWebhookEventRepository repository = mock(CelcoinWebhookEventRepository.class);
        CelcoinWebhookSignatureVerifier verifier =
                new CelcoinWebhookSignatureVerifier(TestProperties.celcoin("http://localhost"));
        CelcoinWebhookService service =
                new CelcoinWebhookService(repository, verifier, TestProperties.celcoin("http://localhost"), new ObjectMapper());
        byte[] payload = "{\"id\":\"evt-1\",\"type\":\"pix.cashin\"}".getBytes(StandardCharsets.UTF_8);

        ArgumentCaptor<CelcoinWebhookEvent> eventCaptor = ArgumentCaptor.forClass(CelcoinWebhookEvent.class);
        when(repository.findByExternalEventId("evt-1")).thenReturn(Optional.empty());
        when(repository.save(eventCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findById(any())).thenReturn(Optional.empty());

        var first = service.receive(payload, new HttpHeaders());
        CelcoinWebhookEvent saved = eventCaptor.getValue();
        when(repository.findByExternalEventId("evt-1")).thenReturn(Optional.of(saved));

        var second = service.receive(payload, new HttpHeaders());

        assertThat(first.duplicate()).isFalse();
        assertThat(second.duplicate()).isTrue();
        assertThat(second.externalEventId()).isEqualTo("evt-1");
    }
}
