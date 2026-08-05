package com.brunopedraca.celcoin.common.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.brunopedraca.celcoin.config.CelcoinProperties.SslProperties;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NettyCelcoinSslContextProviderTest {
    @TempDir
    Path tempDir;

    @Test
    void buildsClientContextWhenNoStoresConfigured() {
        NettyCelcoinSslContextProvider provider = new NettyCelcoinSslContextProvider(
                new SslProperties(true, null, "PKCS12", null, null, null, "PKCS12", null));

        assertThat(provider.createSslContext()).isNotNull();
    }

    @Test
    void buildsContextFromKeystoreAndTruststore() throws Exception {
        Path keystore = createEmptyStore("keystore.p12", "kspass");
        Path truststore = createEmptyStore("truststore.p12", "tspass");
        SslProperties ssl = new SslProperties(
                true, keystore.toString(), "PKCS12", "kspass", "kspass", truststore.toString(), "PKCS12", "tspass");

        NettyCelcoinSslContextProvider provider = new NettyCelcoinSslContextProvider(ssl);

        assertThat(provider.createSslContext()).isNotNull();
    }

    @Test
    void failsWhenKeystoreDoesNotExist() {
        SslProperties ssl = new SslProperties(
                true, tempDir.resolve("missing.p12").toString(), "PKCS12", "kspass", null, null, "PKCS12", null);

        NettyCelcoinSslContextProvider provider = new NettyCelcoinSslContextProvider(ssl);

        assertThatThrownBy(provider::createSslContext)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("keystore");
    }

    @Test
    void failsWhenTruststoreDoesNotExist() {
        SslProperties ssl = new SslProperties(
                true, null, "PKCS12", null, null, tempDir.resolve("missing.p12").toString(), "PKCS12", "tspass");

        NettyCelcoinSslContextProvider provider = new NettyCelcoinSslContextProvider(ssl);

        assertThatThrownBy(provider::createSslContext)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("truststore");
    }

    private Path createEmptyStore(String name, String password) throws Exception {
        Path path = tempDir.resolve(name);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        try (OutputStream out = Files.newOutputStream(path)) {
            keyStore.store(out, password.toCharArray());
        }
        return path;
    }
}
