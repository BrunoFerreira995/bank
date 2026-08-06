import { apiRequest } from "@/core/http/api-client";

export type Address = {
  postalCode: string;
  street: string;
  number: string;
  city: string;
  state: string;
};

export type PersonRegistration = {
  personType: "PF" | "PJ";
  document: string;
  email: string;
  phone: string;
  address: Address;
  representatives?: Array<{ name: string; document: string }>;
  partners?: Array<{ name: string; document: string; ownershipPercentage: number }>;
};

export type RegistrationResponse = { onboardingId: string; status: string };

export function createRegistration(payload: PersonRegistration): Promise<RegistrationResponse> {
  return apiRequest<RegistrationResponse>("/mobile/v1/onboardings", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}
