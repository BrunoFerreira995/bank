import { apiRequest } from "@/core/http/api-client";

export type Consent = {
  type: "TERMS_OF_USE" | "PRIVACY_POLICY" | "OPEN_FINANCE";
  version: string;
  accepted: boolean;
};

export function acceptConsents(consents: Consent[]): Promise<void> {
  return apiRequest<void>("/mobile/v1/consents", {
    method: "POST",
    body: JSON.stringify({ consents }),
  });
}
