import { apiRequest } from "@/core/http/api-client";

export type EscrowStatus = "PENDING" | "ACTIVE" | "RELEASED" | "CANCELLED" | "UNDER_REVIEW";
export type Escrow = {
  id: string;
  status: EscrowStatus;
  balance: number;
  currency: "BRL";
  parties: Array<{ name: string; role: string }>;
  updatedAt: string;
};
export type EscrowEvent = {
  id: string;
  type: string;
  amount?: number;
  status: string;
  createdAt: string;
};

export const listEscrows = () => apiRequest<Escrow[]>("/mobile/v1/escrow");
export const getEscrow = (id: string) =>
  apiRequest<Escrow>(`/mobile/v1/escrow/${encodeURIComponent(id)}`);
export const getEscrowEvents = (id: string) =>
  apiRequest<EscrowEvent[]>(`/mobile/v1/escrow/${encodeURIComponent(id)}/events`);
