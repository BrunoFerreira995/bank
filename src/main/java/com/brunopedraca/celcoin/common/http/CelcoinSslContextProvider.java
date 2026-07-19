package com.brunopedraca.celcoin.common.http;

import io.netty.handler.ssl.SslContext;

public interface CelcoinSslContextProvider {
    SslContext createSslContext();
}
