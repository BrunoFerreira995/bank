package com.brunopedraca.celcoin.common.http;

public record CelcoinApiResponse<T>(T data, String correlationId, String remoteRequestId) {}
