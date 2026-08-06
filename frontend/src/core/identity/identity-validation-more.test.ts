import { canCaptureDocument } from "@/core/onboarding/capture-policy";
import { useSessionStore } from "@/core/auth/session-store";

describe("capture policy", () => {
  beforeEach(() => useSessionStore.setState({ session: null, hydrated: true }));

  it("blocks capture without an authenticated session", () => {
    expect(canCaptureDocument(true, false)).toEqual({ allowed: false, reason: "SESSION_EXPIRED" });
  });

  it("blocks denied permission or compromised devices", () => {
    useSessionStore.setState({ session: { accessToken: "test" } });
    expect(canCaptureDocument(false, false)).toEqual({
      allowed: false,
      reason: "PERMISSION_DENIED",
    });
    expect(canCaptureDocument(true, true)).toEqual({
      allowed: false,
      reason: "COMPROMISED_DEVICE",
    });
  });
});
