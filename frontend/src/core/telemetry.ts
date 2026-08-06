import { redactError, redactProperties } from "@/core/security/redaction";

type TelemetryProperties = Record<string, string | number | boolean | undefined>;

export function track(event: string, properties: TelemetryProperties = {}): void {
  if (__DEV__) {
    // Deliberately limited to development; production uses the BFF/observability SDK.
    console.debug(`[telemetry] ${event}`, redactProperties(properties));
  }
}

export function reportError(error: unknown, context: TelemetryProperties = {}): void {
  if (__DEV__) console.warn("[error]", redactError(error), redactProperties(context));
  // Production integration must redact sensitive fields before sending to Sentry/observability.
}
