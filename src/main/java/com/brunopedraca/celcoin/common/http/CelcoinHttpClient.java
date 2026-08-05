package com.brunopedraca.celcoin.common.http;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.exception.CelcoinRateLimitException;
import com.brunopedraca.celcoin.common.idempotency.CelcoinIdempotencyService;
import com.brunopedraca.celcoin.common.validation.SensitiveDataMasker;
import com.brunopedraca.celcoin.config.CelcoinProperties;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class CelcoinHttpClient {
    private final WebClient webClient;
    private final CelcoinProperties properties;
    private final CelcoinIdempotencyService idempotencyService;

    public CelcoinHttpClient(WebClient webClient, CelcoinProperties properties) {
        this(webClient, properties, null);
    }

    public CelcoinHttpClient(
            WebClient webClient, CelcoinProperties properties, CelcoinIdempotencyService idempotencyService) {
        this.webClient = webClient;
        this.properties = properties;
        this.idempotencyService = idempotencyService;
    }

    public <T> T get(String path, Class<T> responseType, CelcoinRequestContext context) {
        return exchange(HttpMethod.GET, path, null, responseType, context, true);
    }

    public <T> T post(String path, Object body, Class<T> responseType, CelcoinRequestContext context) {
        boolean idempotent = context != null
                && context.idempotencyKey() != null
                && !context.idempotencyKey().isBlank();
        return exchange(HttpMethod.POST, path, body, responseType, context, idempotent);
    }

    public <T> T put(String path, Object body, Class<T> responseType, CelcoinRequestContext context) {
        boolean idempotent = context != null
                && context.idempotencyKey() != null
                && !context.idempotencyKey().isBlank();
        return exchange(HttpMethod.PUT, path, body, responseType, context, idempotent);
    }

    public <T> T delete(String path, Object body, Class<T> responseType, CelcoinRequestContext context) {
        boolean idempotent = context != null
                && context.idempotencyKey() != null
                && !context.idempotencyKey().isBlank();
        return exchange(HttpMethod.DELETE, path, body, responseType, context, idempotent);
    }

    public byte[] download(String path, CelcoinRequestContext context) {
        return webClient
                .get()
                .uri(java.net.URI.create(absolute(path)))
                .header("X-Correlation-Id", correlation(context))
                .accept(MediaType.APPLICATION_PDF, MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return bytes;
                })
                .reduce(new byte[0], (left, right) -> {
                    byte[] merged = new byte[left.length + right.length];
                    System.arraycopy(left, 0, merged, 0, left.length);
                    System.arraycopy(right, 0, merged, left.length, right.length);
                    return merged;
                })
                .block(properties.readTimeout().plusSeconds(5));
    }

    private <T> T exchange(
            HttpMethod method,
            String path,
            Object body,
            Class<T> responseType,
            CelcoinRequestContext context,
            boolean retryAllowed) {
        String idempotencyKey = context == null ? null : context.idempotencyKey();
        boolean idempotent =
                idempotencyService != null && idempotencyKey != null && !idempotencyKey.isBlank() && retryAllowed;
        Optional<String> replayed =
                idempotent ? idempotencyService.begin(idempotencyKey, path, body) : Optional.empty();
        if (replayed.isPresent()) {
            return idempotencyService.deserialize(replayed.get(), responseType);
        }
        try {
            WebClient.RequestBodySpec spec = webClient
                    .method(method)
                    .uri(java.net.URI.create(absolute(path)))
                    .header("X-Correlation-Id", correlation(context));
            if (idempotencyKey != null) {
                spec.header("Idempotency-Key", idempotencyKey);
            }
            Mono<T> mono = spec.bodyValue(Objects.requireNonNullElse(body, ""))
                    .retrieve()
                    .onStatus(status -> status.value() == 429, response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(payload -> new CelcoinRateLimitException(
                                    "Rate limit exceeded by Celcoin API: " + SensitiveDataMasker.mask(payload),
                                    RateLimitInfo.from(response.headers().asHttpHeaders()))))
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(payload -> new CelcoinApiException(
                                            SensitiveDataMasker.mask(payload.isBlank() ? "Celcoin API error" : payload),
                                            response.statusCode(),
                                            correlation(context),
                                            response.headers().asHttpHeaders().getFirst("X-Request-Id"))))
                    .bodyToMono(responseType);
            if (retryAllowed) {
                Duration backoff = properties.retry().initialBackoff();
                if (backoff.compareTo(Duration.ofMillis(1)) < 0) {
                    backoff = Duration.ofMillis(1);
                }
                mono = mono.retryWhen(
                        Retry.backoff(Math.max(0, properties.retry().maxAttempts() - 1), backoff)
                                .filter(this::isTransient)
                                .onRetryExhaustedThrow((retrySpec, retrySignal) -> retrySignal.failure()));
            }
            T result = mono.block(properties.readTimeout().plusSeconds(5));
            if (idempotent) {
                idempotencyService.complete(idempotencyKey, result);
            }
            return result;
        } catch (CelcoinApiException e) {
            if (idempotent) {
                idempotencyService.fail(idempotencyKey, e.getMessage());
            }
            throw e;
        } catch (CelcoinRateLimitException e) {
            if (idempotent) {
                idempotencyService.fail(idempotencyKey, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            if (idempotent) {
                idempotencyService.fail(idempotencyKey, e.getMessage());
            }
            throw new CelcoinIntegrationException(
                    "Celcoin HTTP call failed: " + SensitiveDataMasker.mask(e.getMessage()), e);
        }
    }

    private String absolute(String path) {
        String base = properties.baseUrl() == null ? "" : properties.baseUrl();
        return base + path;
    }

    private boolean isTransient(Throwable throwable) {
        if (throwable instanceof CelcoinApiException apiException && apiException.status() != null) {
            int code = apiException.status().value();
            return code == 408 || code == 429 || code >= 500;
        }
        return false;
    }

    private String correlation(CelcoinRequestContext context) {
        return context == null || context.correlationId() == null ? "celcoin-sdk" : context.correlationId();
    }
}
