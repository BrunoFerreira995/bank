import { apiRequest } from "@/core/http/api-client";

export type CardStatus = "PENDING" | "ACTIVE" | "BLOCKED" | "CANCELLED";
export type Card = {
  id: string;
  type: "VIRTUAL" | "PHYSICAL";
  lastFour: string;
  brand: string;
  status: CardStatus;
  limit: number;
  availableLimit: number;
};
export type CardTransaction = {
  id: string;
  description: string;
  amount: number;
  status: string;
  occurredAt: string;
};
export type CardStatement = { dueDate: string; total: number; minimum: number; status: string };

export const listCards = () => apiRequest<Card[]>("/mobile/v1/cards");
export const requestCard = (type: Card["type"]) =>
  apiRequest<Card>("/mobile/v1/cards", { method: "POST", body: JSON.stringify({ type }) });
export const activateCard = (cardId: string, lastFour: string) =>
  apiRequest<Card>(`/mobile/v1/cards/${encodeURIComponent(cardId)}/activation`, {
    method: "POST",
    body: JSON.stringify({ lastFour }),
  });
export const setCardBlocked = (cardId: string, blocked: boolean) =>
  apiRequest<Card>(
    `/mobile/v1/cards/${encodeURIComponent(cardId)}/${blocked ? "block" : "unblock"}`,
    { method: "POST" },
  );
export const getCardStatement = (cardId: string) =>
  apiRequest<CardStatement>(`/mobile/v1/cards/${encodeURIComponent(cardId)}/statement`);
export const getCardTransactions = (cardId: string) =>
  apiRequest<CardTransaction[]>(`/mobile/v1/cards/${encodeURIComponent(cardId)}/transactions`);

export function maskPan(value: string): string {
  const digits = value.replace(/\D/g, "");
  return digits.length < 4 ? "••••" : `•••• •••• •••• ${digits.slice(-4)}`;
}

export function maskCvv(): string {
  return "•••";
}
