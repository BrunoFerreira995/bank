package com.brunopedraca.celcoin.movement;

import com.brunopedraca.celcoin.movement.MovementFileDtos.SftpDownloadRequest;
import com.brunopedraca.celcoin.movement.MovementFileDtos.SftpDownloadResponse;

public interface CelcoinMovementFileOperations {
    SftpDownloadResponse download(SftpDownloadRequest request);
}
