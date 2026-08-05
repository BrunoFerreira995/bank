package com.brunopedraca.celcoin.common.http;

import com.brunopedraca.celcoin.config.CelcoinProperties.SslProperties;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Default mTLS provider that builds a Netty {@link SslContext} from PKCS12/JKS
 * keystore and truststore paths configured under {@code celcoin.ssl}.
 */
public class NettyCelcoinSslContextProvider implements CelcoinSslContextProvider {
    private final SslProperties ssl;

    public NettyCelcoinSslContextProvider(SslProperties ssl) {
        this.ssl = ssl;
    }

    @Override
    public SslContext createSslContext() {
        try {
            SslContextBuilder builder = SslContextBuilder.forClient();
            if (hasText(ssl.keystorePath())) {
                builder.keyManager(loadKeyManagerFactory());
            }
            if (hasText(ssl.truststorePath())) {
                builder.trustManager(loadTrustManagerFactory());
            }
            return builder.build();
        } catch (javax.net.ssl.SSLException e) {
            throw new IllegalStateException("Unable to build Celcoin mTLS context", e);
        }
    }

    private KeyManagerFactory loadKeyManagerFactory() {
        try (InputStream in = Files.newInputStream(Paths.get(ssl.keystorePath()))) {
            KeyStore keyStore = KeyStore.getInstance(ssl.keystoreType());
            char[] password = passwordOf(ssl.keystorePassword());
            keyStore.load(in, password);
            KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore, passwordOf(ssl.keyPassword() != null ? ssl.keyPassword() : ssl.keystorePassword()));
            return factory;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load Celcoin client keystore", e);
        }
    }

    private TrustManagerFactory loadTrustManagerFactory() {
        try (InputStream in = Files.newInputStream(Paths.get(ssl.truststorePath()))) {
            KeyStore trustStore = KeyStore.getInstance(ssl.truststoreType());
            char[] password = passwordOf(ssl.truststorePassword());
            trustStore.load(in, password);
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(trustStore);
            return factory;
        } catch (IOException | java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Unable to load Celcoin truststore", e);
        }
    }

    private char[] passwordOf(String password) {
        return password == null ? new char[0] : password.toCharArray();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
