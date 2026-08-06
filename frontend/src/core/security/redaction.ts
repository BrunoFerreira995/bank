const SENSITIVE_KEYS =
  /token|secret|password|client.?secret|authorization|cpf|cnpj|pan|cvv|document|certificate/i;

export function redactProperties(value: unknown): unknown {
  if (Array.isArray(value)) return value.map(redactProperties);
  if (!value || typeof value !== "object") return value;
  return Object.fromEntries(
    Object.entries(value).map(([key, item]) => [
      key,
      SENSITIVE_KEYS.test(key) ? "[REDACTED]" : redactProperties(item),
    ]),
  );
}

export function redactError(error: unknown): { message: string; details?: unknown } {
  if (error instanceof Error)
    return {
      message: error.message,
      details: redactProperties({ name: error.name, stack: error.stack }),
    };
  return { message: "Unknown error", details: redactProperties(error) };
}
