import { apiRequest } from "@/core/http/api-client";

export type LoginRequest = { identifier: string; password: string };
export type LoginResponse = {
  accessToken?: string;
  refreshToken?: string;
  mfaRequired?: boolean;
  challengeId?: string;
};

export type RecoveryResponse = { accepted: boolean };
export type MfaRequest = { challengeId: string; code: string };

export function login(request: LoginRequest): Promise<LoginResponse> {
  return apiRequest<LoginResponse>("/mobile/v1/session", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export function recoverAccess(identifier: string): Promise<RecoveryResponse> {
  return apiRequest<RecoveryResponse>("/mobile/v1/access-recovery", {
    method: "POST",
    body: JSON.stringify({ identifier }),
  });
}

export function verifyMfa(request: MfaRequest): Promise<LoginResponse> {
  return apiRequest<LoginResponse>("/mobile/v1/session/mfa", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  return apiRequest<void>("/mobile/v1/password", {
    method: "PUT",
    body: JSON.stringify({ currentPassword, newPassword }),
  });
}
