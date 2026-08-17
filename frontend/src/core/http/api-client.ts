import { env } from "@/config/env";
import { useSessionStore } from "@/core/auth/session-store";

export type ApiRequestOptions = RequestInit & {
  idempotencyKey?: string;
  timeoutMs?: number;
};

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly code?: string,
    readonly details?: unknown,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

function makeIdempotencyKey(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 12)}`;
}

export async function apiRequest<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
  if (!path.startsWith("/")) throw new Error("BFF path must start with '/'");
  const session = useSessionStore.getState().session;
  const headers = new Headers(options.headers);
  headers.set("Accept", "application/json");
  if (options.body && !headers.has("Content-Type")) headers.set("Content-Type", "application/json");
  if (session?.accessToken) headers.set("Authorization", `Bearer ${session.accessToken}`);
  if (options.idempotencyKey) headers.set("Idempotency-Key", options.idempotencyKey);
  else if (options.method && !["GET", "HEAD"].includes(options.method.toUpperCase())) {
    headers.set("Idempotency-Key", makeIdempotencyKey());
  }

  const attempts = 2;
  for (let attempt = 1; attempt <= attempts; attempt += 1) {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), options.timeoutMs ?? 15_000);
    try {
      const response = await fetch(`${env.bffBaseUrl}${path}`, {
        ...options,
        headers,
        signal: controller.signal,
      });
      const text = await response.text();
      const body = text ? parseBody(text) : undefined;
      if (response.ok) return body as T;
      if (response.status === 401 && session) {
        await useSessionStore.getState().expire();
      }
      if (response.status >= 500 && attempt < attempts) {
        await wait(150 * attempt);
        continue;
      }
      const record = isRecord(body) ? body : {};
      throw new ApiError(
        typeof record.message === "string" ? record.message : "Request failed",
        response.status,
        typeof record.code === "string" ? record.code : undefined,
        record,
      );
    } catch (error) {
      if (attempt < attempts && !(error instanceof ApiError)) {
        await wait(150 * attempt);
        continue;
      }
      throw error;
    } finally {
      clearTimeout(timeout);
    }
  }
  throw new Error("Unreachable request state");
}

function wait(milliseconds: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
}

function parseBody(text: string): unknown {
  try {
    return JSON.parse(text) as unknown;
  } catch {
    return text;
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}
