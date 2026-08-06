package com.brunopedraca.celcoin.movement;

import jakarta.validation.constraints.NotBlank;

public final class MovementFileDtos {
    private MovementFileDtos() {}

    public record SftpDownloadRequest(@NotBlank String remotePath, @NotBlank String localPath) {}

    public record SftpDownloadResponse(String localPath, long size, String sha256) {}
}
