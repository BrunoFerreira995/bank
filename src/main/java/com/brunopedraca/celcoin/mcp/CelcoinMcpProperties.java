package com.brunopedraca.celcoin.mcp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "celcoin.mcp")
public record CelcoinMcpProperties(boolean enabled, String path, String serverName, String serverVersion) {
    public CelcoinMcpProperties {
        if (path == null || path.isBlank()) path = "/mcp";
        if (serverName == null || serverName.isBlank()) serverName = "celcoin-spring-sdk";
        if (serverVersion == null || serverVersion.isBlank()) serverVersion = "0.1.0";
    }
}
