import { decodePixPayload } from "./pix-emv";

describe("Pix EMV", () => {
  it("rejects malformed payloads", () => {
    expect(() => decodePixPayload("abc")).toThrow("QR Code Pix inválido");
  });

  it("reads the top-level Pix payload fields", () => {
    expect(
      decodePixPayload("000201010212520400005303986540510.005905Teste6009SAO PAULO"),
    ).toMatchObject({ amount: "10.00", merchantName: "Teste", city: "SAO PAULO" });
  });
});
