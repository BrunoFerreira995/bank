package com.brunopedraca.celcoin.movement;

import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.movement.MovementFileDtos.SftpDownloadRequest;
import com.brunopedraca.celcoin.movement.MovementFileDtos.SftpDownloadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StringUtils;

/** Downloads Celcoin movement files through the system OpenSSH SFTP client. */
public final class SftpMovementFileClient implements CelcoinMovementFileOperations {
    private final String host;
    private final int port;
    private final String username;
    private final Path privateKey;
    private final Path knownHosts;
    private final Duration timeout;

    public SftpMovementFileClient(
            String host,
            int port,
            String username,
            Path privateKey,
            Path knownHosts,
            Duration timeout) {
        if (!StringUtils.hasText(host) || !StringUtils.hasText(username)) {
            throw new IllegalArgumentException("SFTP host and username are required");
        }
        this.host = host;
        this.port = port > 0 ? port : 22;
        this.username = username;
        this.privateKey = Objects.requireNonNull(privateKey, "privateKey is required");
        this.knownHosts = Objects.requireNonNull(knownHosts, "knownHosts is required");
        this.timeout = timeout == null ? Duration.ofMinutes(2) : timeout;
    }

    @Override
    public SftpDownloadResponse download(SftpDownloadRequest request) {
        Objects.requireNonNull(request, "request is required");
        if (request.remotePath().contains("\n") || request.remotePath().contains("\r")) {
            throw new IllegalArgumentException("remotePath must not contain line breaks");
        }
        Path destination = Path.of(request.localPath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(destination.getParent());
            Process process = new ProcessBuilder(
                            "sftp",
                            "-oBatchMode=yes",
                            "-oStrictHostKeyChecking=yes",
                            "-oUserKnownHostsFile=" + knownHosts.toAbsolutePath(),
                            "-P",
                            String.valueOf(port),
                            "-i",
                            privateKey.toAbsolutePath().toString(),
                            username + "@" + host)
                    .redirectErrorStream(true)
                    .start();
            process.getOutputStream().write(("get " + request.remotePath() + " " + destination + "\n").getBytes());
            process.getOutputStream().close();
            if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new CelcoinIntegrationException("SFTP movement file download timed out");
            }
            String output = new String(process.getInputStream().readAllBytes());
            if (process.exitValue() != 0 || !Files.isRegularFile(destination)) {
                throw new CelcoinIntegrationException("SFTP movement file download failed: " + output.trim());
            }
            return new SftpDownloadResponse(
                    destination.toString(), Files.size(destination), sha256(destination));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CelcoinIntegrationException("SFTP movement file download interrupted");
        } catch (IOException exception) {
            throw new CelcoinIntegrationException("Unable to execute SFTP movement file download", exception);
        }
    }

    private String sha256(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) >= 0) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
