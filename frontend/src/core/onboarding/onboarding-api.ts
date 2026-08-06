import { apiRequest } from "@/core/http/api-client";

export type OnboardingStatus =
  | "PENDING"
  | "BIOMETRIC_LIVENESS"
  | "BIOMETRIC_DOC_LIVENESS"
  | "APPROVED"
  | "REJECTED"
  | "NEEDS_CORRECTION";

export type OnboardingSummary = {
  id: string;
  personType: "PF" | "PJ";
  status: OnboardingStatus;
  reason?: string;
};

export function getOnboardingStatus(id: string): Promise<OnboardingSummary> {
  return apiRequest<OnboardingSummary>(`/mobile/v1/onboardings/${encodeURIComponent(id)}`);
}
