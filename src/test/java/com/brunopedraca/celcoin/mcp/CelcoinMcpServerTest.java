package com.brunopedraca.celcoin.mcp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.banking.CelcoinAccountOperations;
import com.brunopedraca.celcoin.pix.CelcoinPixOperations;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CelcoinMcpServerTest {
    private CelcoinClient client;
    private CelcoinPixOperations pix;
    private CelcoinAccountOperations accounts;
    private CelcoinMcpServer server;

    @BeforeEach
    void setUp() {
        client = mock(CelcoinClient.class);
        pix = mock(CelcoinPixOperations.class);
        accounts = mock(CelcoinAccountOperations.class);
        when(client.pix()).thenReturn(pix);
        when(client.accounts()).thenReturn(accounts);
        server = new CelcoinMcpServer(client, new CelcoinMcpProperties(true, "/mcp", "test", "1"));
    }

    @Test
    void initializesAndListsTools() {
        var initialize = server.handle(new CelcoinMcpServer.McpRequest(1, "initialize", Map.of()));
        var tools = server.handle(new CelcoinMcpServer.McpRequest(2, "tools/list", Map.of()));

        assertThat(initialize.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(initialize.getBody().result()).isInstanceOf(Map.class);
        assertThat(tools.getBody().result()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) tools.getBody().result()).get("tools")).isInstanceOf(List.class);
    }

    @Test
    void handlesNotificationsWithoutResponseBody() {
        var response = server.handle(new CelcoinMcpServer.McpRequest(null, "notifications/initialized", Map.of()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void callsParticipantsTool() {
        when(pix.participants()).thenReturn(List.of());

        var response = server.handle(new CelcoinMcpServer.McpRequest(
                3, "tools/call", Map.of("name", "pix_participants", "arguments", Map.of())));

        assertThat(response.getBody().error()).isNull();
        assertThat(response.getBody().result()).isInstanceOf(Map.class);
    }

    @Test
    void returnsToolErrorForMissingArgument() {
        var response = server.handle(new CelcoinMcpServer.McpRequest(
                4, "tools/call", Map.of("name", "account_balance", "arguments", Map.of())));

        Map<?, ?> result = (Map<?, ?>) response.getBody().result();
        assertThat(result.get("isError")).isEqualTo(true);
    }

    @Test
    void reportsUnknownMethod() {
        var response = server.handle(new CelcoinMcpServer.McpRequest(5, "unknown", Map.of()));

        assertThat(response.getBody().error().code()).isEqualTo(-32601);
    }
}
