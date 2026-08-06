import { redactError, redactProperties } from "./redaction";

describe("observability redaction", () => {
  it("removes credentials and personal/card data recursively", () => {
    expect(
      redactProperties({
        token: "abc",
        customer: { cpf: "123", name: "Ana" },
        card: { pan: "4111", lastFour: "1111" },
      }),
    ).toEqual({
      token: "[REDACTED]",
      customer: { cpf: "[REDACTED]", name: "Ana" },
      card: { pan: "[REDACTED]", lastFour: "1111" },
    });
  });

  it("returns a safe error shape", () => {
    expect(redactError({ password: "secret" }).details).toEqual({ password: "[REDACTED]" });
  });
});
