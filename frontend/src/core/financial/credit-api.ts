import { apiRequest } from "@/core/http/api-client";

export type CreditAnalysis = "PENDING" | "APPROVED" | "REJECTED" | "UNAVAILABLE";
export type CreditSimulation = {
  amount: number;
  installments: number;
  installmentAmount: number;
  totalAmount: number;
  rate: number;
};
export type CreditProposal = {
  id: string;
  product: string;
  status: CreditAnalysis;
  simulation?: CreditSimulation;
  requiredDocuments?: string[];
};
export type CreditPortability = {
  id: string;
  creditor: string;
  status: "PENDING" | "IN_ANALYSIS" | "COMPLETED" | "REJECTED";
  updatedAt: string;
};

export const simulateCredit = (payload: {
  product: string;
  amount: number;
  installments: number;
}) =>
  apiRequest<CreditSimulation>("/mobile/v1/credit/simulations", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const createCreditProposal = (payload: { product: string; simulation: CreditSimulation }) =>
  apiRequest<CreditProposal>("/mobile/v1/credit/proposals", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const getCreditProposal = (id: string) =>
  apiRequest<CreditProposal>(`/mobile/v1/credit/proposals/${encodeURIComponent(id)}`);
export const uploadCreditDocument = (proposalId: string, documentUrl: string) =>
  apiRequest<void>(`/mobile/v1/credit/proposals/${encodeURIComponent(proposalId)}/documents`, {
    method: "POST",
    body: JSON.stringify({ documentUrl }),
  });
export const createWorkerConsignedProposal = (payload: object) =>
  apiRequest<CreditProposal>("/mobile/v1/credit/consigned/worker", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const createArmyConsignedProposal = (payload: object) =>
  apiRequest<CreditProposal>("/mobile/v1/credit/consigned/army", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const requestCreditPortability = (payload: {
  creditor: string;
  contract: string;
  balance: number;
}) =>
  apiRequest<CreditPortability>("/mobile/v1/credit/portability", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const getCreditPortability = (id: string) =>
  apiRequest<CreditPortability>(`/mobile/v1/credit/portability/${encodeURIComponent(id)}`);
