package com.brunopedraca.celcoin.mcp;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixEmvDecodeResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Small, opt-in MCP JSON-RPC adapter for the public SDK operations.
 *
 * <p>This is an integration adapter owned by this SDK; it is not an official Celcoin MCP server.
 */
@RestController
@ConditionalOnProperty(prefix = "celcoin.mcp", name = "enabled", havingValue = "true")
@RequestMapping("${celcoin.mcp.path:/mcp}")
public final class CelcoinMcpServer {
    private final CelcoinClient client;
    private final CelcoinMcpProperties properties;

    public CelcoinMcpServer(CelcoinClient client, CelcoinMcpProperties properties) {
        this.client = Objects.requireNonNull(client, "client is required");
        this.properties = Objects.requireNonNull(properties, "properties is required");
    }

    @PostMapping
    public ResponseEntity<McpResponse> handle(@RequestBody McpRequest request) {
        if (request == null || request.method() == null) {
            return ResponseEntity.ok(McpResponse.error(request == null ? null : request.id(), -32600, "Invalid request"));
        }
        return switch (request.method()) {
            case "initialize" -> ResponseEntity.ok(McpResponse.success(request.id(), Map.of(
                    "protocolVersion", "2025-06-18",
                    "capabilities", Map.of("tools", Map.of()),
                    "serverInfo", Map.of("name", properties.serverName(), "version", properties.serverVersion()))));
            case "notifications/initialized" -> ResponseEntity.accepted().build();
            case "tools/list" -> ResponseEntity.ok(McpResponse.success(request.id(), Map.of("tools", tools())));
            case "tools/call" -> ResponseEntity.ok(callTool(request));
            default -> ResponseEntity.ok(McpResponse.error(request.id(), -32601, "Method not found: " + request.method()));
        };
    }

    private McpResponse callTool(McpRequest request) {
        Map<String, Object> params = request.params() == null ? Map.of() : request.params();
        String name = String.valueOf(params.getOrDefault("name", ""));
        Map<String, Object> arguments = asMap(params.get("arguments"));
        try {
            Object result = switch (name) {
                case "pix_participants" -> client.pix().participants();
                case "pix_decode_emv" -> decodeEmv(arguments);
                case "account_balance" -> client.accounts().getBalance(required(arguments, "account"));
                default -> throw new IllegalArgumentException("Unknown tool: " + name);
            };
            return McpResponse.success(request.id(), Map.of(
                    "content", List.of(Map.of("type", "text", "text", JsonValue.render(result))),
                    "structuredContent", result));
        } catch (RuntimeException exception) {
            return McpResponse.success(request.id(), Map.of(
                    "isError", true,
                    "content", List.of(Map.of("type", "text", "text", exception.getMessage()))));
        }
    }

    private Object decodeEmv(Map<String, Object> arguments) {
        CelcoinPixEmvDecodeResponse response = client.pix().decodeEmv(required(arguments, "emv"));
        return response;
    }

    private static List<Map<String, Object>> tools() {
        return List.of(
                tool("pix_participants", "Lista participantes Pix do SPI", Map.of("type", "object")),
                tool("pix_decode_emv", "Decodifica um payload EMV Pix", objectProperty("emv")),
                tool("account_balance", "Consulta o saldo de uma conta Celcoin", objectProperty("account")));
    }

    private static Map<String, Object> tool(String name, String description, Map<String, Object> inputSchema) {
        return Map.of("name", name, "description", description, "inputSchema", inputSchema);
    }

    private static Map<String, Object> objectProperty(String requiredProperty) {
        return Map.of(
                "type", "object",
                "properties", Map.of(requiredProperty, Map.of("type", "string")),
                "required", List.of(requiredProperty));
    }

    private static Map<String, Object> asMap(Object value) {
        if (value == null) return Map.of();
        if (value instanceof Map<?, ?> map) {
            java.util.HashMap<String, Object> result = new java.util.HashMap<>();
            map.forEach((key, item) -> result.put(String.valueOf(key), item));
            return result;
        }
        throw new IllegalArgumentException("arguments must be an object");
    }

    private static String required(Map<String, Object> arguments, String name) {
        Object value = arguments.get(name);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException("Missing required argument: " + name);
        }
        return String.valueOf(value);
    }

    public record McpRequest(Object id, String method, Map<String, Object> params) {}

    public record McpResponse(Object jsonrpc, Object id, Object result, McpError error) {
        static McpResponse success(Object id, Object result) {
            return new McpResponse("2.0", id, result, null);
        }

        static McpResponse error(Object id, int code, String message) {
            return new McpResponse("2.0", id, null, new McpError(code, message));
        }
    }

    public record McpError(int code, String message) {}

    private static final class JsonValue {
        private JsonValue() {}

        static String render(Object value) {
            try {
                return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(value);
            } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
                return String.valueOf(value);
            }
        }
    }
}
