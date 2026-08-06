import { apiRequest } from "@/core/http/api-client";

export type OpenFinanceStatus =
  | "PENDING"
  | "AWAITING_CALLBACK"
  | "AUTHORIZED"
  | "REJECTED"
  | "EXPIRED"
  | "CANCELLED"
  | "TIMEOUT"
  | "DUPLICATED";
export type Institution = {
  id: string;
  name: string;
  logoUrl?: string;
  status: "AVAILABLE" | "UNAVAILABLE";
};
export type Consent = {
  id: string;
  institutionId: string;
  institutionName: string;
  scopes: string[];
  status: OpenFinanceStatus;
  expiresAt: string;
};
export type OpenFinancePayment = {
  id: string;
  amount: number;
  status: OpenFinanceStatus;
  scheduledFor?: string;
  createdAt: string;
};
export type Flow = {
  id: string;
  status: OpenFinanceStatus;
  redirectUrl?: string;
  callbackUrl?: string;
  expiresAt?: string;
};

export const listInstitutions = () =>
  apiRequest<Institution[]>("/mobile/v1/open-finance/institutions");
export const createConsent = (institutionId: string, scopes: string[]) =>
  apiRequest<Consent>("/mobile/v1/open-finance/consents", {
    method: "POST",
    body: JSON.stringify({ institutionId, scopes }),
  });
export const listConsents = () => apiRequest<Consent[]>("/mobile/v1/open-finance/consents");
export const revokeConsent = (consentId: string) =>
  apiRequest<void>(`/mobile/v1/open-finance/consents/${encodeURIComponent(consentId)}/revoke`, {
    method: "POST",
  });
export const listLinks = () => apiRequest<Flow[]>("/mobile/v1/open-finance/links");
export const initiateRedirectFlow = (institutionId: string, consentId: string) =>
  apiRequest<Flow>("/mobile/v1/open-finance/redirect-flows", {
    method: "POST",
    body: JSON.stringify({ institutionId, consentId }),
  });
export const getFlow = (flowId: string) =>
  apiRequest<Flow>(`/mobile/v1/open-finance/flows/${encodeURIComponent(flowId)}`);
export const initiateImmediatePayment = (
  payload: { consentId: string; amount: number; beneficiary: string },
  idempotencyKey: string,
) =>
  apiRequest<OpenFinancePayment>("/mobile/v1/open-finance/payments/immediate", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify(payload),
  });
export const initiateScheduledPayment = (
  payload: { consentId: string; amount: number; beneficiary: string; scheduledFor: string },
  idempotencyKey: string,
) =>
  apiRequest<OpenFinancePayment>("/mobile/v1/open-finance/payments/scheduled", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify(payload),
  });
export const initiateAutomaticPayment = (
  payload: { consentId: string; amount: number; beneficiary: string; frequency: string },
  idempotencyKey: string,
) =>
  apiRequest<OpenFinancePayment>("/mobile/v1/open-finance/payments/automatic", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify(payload),
  });
export const listPayments = () =>
  apiRequest<OpenFinancePayment[]>("/mobile/v1/open-finance/payments");
export const getPayment = (paymentId: string) =>
  apiRequest<OpenFinancePayment>(
    `/mobile/v1/open-finance/payments/${encodeURIComponent(paymentId)}`,
  );
export const cancelPayment = (paymentId: string) =>
  apiRequest<void>(`/mobile/v1/open-finance/payments/${encodeURIComponent(paymentId)}/cancel`, {
    method: "POST",
  });
export const createSweepingTransfer = (
  payload: { sourceAccount: string; targetAccount: string; amount: number },
  idempotencyKey: string,
) =>
  apiRequest<OpenFinancePayment>("/mobile/v1/open-finance/sweeping/transfers", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify(payload),
  });
export const listSweepingTransfers = () =>
  apiRequest<OpenFinancePayment[]>("/mobile/v1/open-finance/sweeping/transfers");
export const createBrickSession = (product: "BANK" | "INSURANCE") =>
  apiRequest<Flow>("/mobile/v1/open-finance/bricks/sessions", {
    method: "POST",
    body: JSON.stringify({ product }),
  });
