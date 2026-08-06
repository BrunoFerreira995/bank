import { useSessionStore } from "@/core/auth/session-store";

export type CaptureBlockReason = "SESSION_EXPIRED" | "PERMISSION_DENIED" | "COMPROMISED_DEVICE";

export function canCaptureDocument(
  permissionGranted: boolean,
  deviceCompromised: boolean,
): { allowed: true } | { allowed: false; reason: CaptureBlockReason } {
  if (!useSessionStore.getState().session) return { allowed: false, reason: "SESSION_EXPIRED" };
  if (!permissionGranted) return { allowed: false, reason: "PERMISSION_DENIED" };
  if (deviceCompromised) return { allowed: false, reason: "COMPROMISED_DEVICE" };
  return { allowed: true };
}
