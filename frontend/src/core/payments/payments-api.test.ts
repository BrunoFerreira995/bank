import { validateAmount, validateBillCode } from "./payments-api";

describe("bill and payment validation", () => {
  it("accepts standard barcode and digitable line lengths", () => {
    expect(validateBillCode("00190500954014481606906809350314337370000000100")).toBe(true);
    expect(validateBillCode("123")).toBe(false);
  });

  it("rejects zero, negative and non-numeric amounts", () => {
    expect(validateAmount(10)).toBe(true);
    expect(validateAmount(0)).toBe(false);
    expect(validateAmount(Number.NaN)).toBe(false);
  });
});
