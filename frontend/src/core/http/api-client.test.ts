import { apiRequest } from "./api-client";

describe("apiRequest", () => {
  beforeEach(() => {
    jest.restoreAllMocks();
  });

  it("adds authorization and idempotency headers for mutations", async () => {
    globalThis.fetch = jest
      .fn()
      .mockResolvedValue(new Response(JSON.stringify({ ok: true }), { status: 200 })) as jest.Mock;
    const result = await apiRequest<{ ok: boolean }>("/mobile/v1/pix/payments", {
      method: "POST",
      body: JSON.stringify({ amount: 1 }),
      idempotencyKey: "test-key",
    });
    expect(result.ok).toBe(true);
    const [, request] = (globalThis.fetch as jest.Mock).mock.calls[0] as [string, RequestInit];
    expect(new Headers(request.headers).get("Idempotency-Key")).toBe("test-key");
  });

  it("maps BFF errors to ApiError", async () => {
    globalThis.fetch = jest.fn().mockResolvedValue(
      new Response(JSON.stringify({ code: "PAGAMENTO_EM_ANALISE", message: "Em análise" }), {
        status: 422,
      }),
    ) as jest.Mock;
    await expect(apiRequest("/mobile/v1/pix/payments", { method: "POST" })).rejects.toEqual(
      expect.objectContaining({ status: 422, code: "PAGAMENTO_EM_ANALISE" }),
    );
  });

  it("retries a transient server failure with the same request", async () => {
    globalThis.fetch = jest
      .fn()
      .mockResolvedValueOnce(
        new Response(JSON.stringify({ message: "temporary" }), { status: 503 }),
      )
      .mockResolvedValueOnce(
        new Response(JSON.stringify({ ok: true }), { status: 200 }),
      ) as jest.Mock;

    await expect(apiRequest("/mobile/v1/accounts", { method: "GET" })).resolves.toEqual({
      ok: true,
    });
    expect(globalThis.fetch).toHaveBeenCalledTimes(2);
  });
});
