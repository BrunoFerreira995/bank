import { canUsePasskey } from "./passkey";

describe("Open Finance native fallback", () => {
  it("does not claim passkey support when the native module is absent", () => {
    expect(canUsePasskey()).toBe(false);
  });
});
