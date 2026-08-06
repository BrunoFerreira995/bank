import { maskCvv, maskPan } from "./cards-api";

describe("card data protection", () => {
  it("only exposes the last four PAN digits", () => {
    expect(maskPan("4111 1111 1111 1111")).toBe("•••• •••• •••• 1111");
    expect(maskCvv()).toBe("•••");
    expect(maskPan("123")).toBe("••••");
  });
});
