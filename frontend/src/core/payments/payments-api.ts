import { apiRequest } from "@/core/http/api-client";

export type PaymentStatus =
  | "PENDING"
  | "AUTHORIZED"
  | "COMPLETED"
  | "FAILED"
  | "CANCELLED"
  | "UNAVAILABLE"
  | "EXPIRED";
export type Bill = {
  id: string;
  digitableLine: string;
  barcode?: string;
  amount: number;
  dueDate: string;
  status: PaymentStatus;
  beneficiary?: string;
};
export type PaymentResponse = {
  id: string;
  status: PaymentStatus;
  amount: number;
  receiptUrl?: string;
};
export type MobileTopUp = {
  id: string;
  operator: string;
  product: string;
  phone: string;
  amount: number;
  status: PaymentStatus;
};
export type VehicleDebt = {
  id: string;
  type: "IPVA" | "FINES" | "LICENSING";
  description: string;
  amount: number;
  dueDate: string;
  status: "OPEN" | "PAID" | "EXPIRED";
};

export const lookupBill = (code: string) =>
  apiRequest<Bill>(`/mobile/v1/bills/lookup?code=${encodeURIComponent(code)}`);
export const authorizeBill = (billId: string) =>
  apiRequest<PaymentResponse>(`/mobile/v1/bills/${encodeURIComponent(billId)}/authorization`, {
    method: "POST",
  });
export const payBill = (billId: string, idempotencyKey: string) =>
  apiRequest<PaymentResponse>(`/mobile/v1/bills/${encodeURIComponent(billId)}/payment`, {
    method: "POST",
    idempotencyKey,
  });
export const getBillPayment = (paymentId: string) =>
  apiRequest<PaymentResponse>(`/mobile/v1/bills/payments/${encodeURIComponent(paymentId)}`);
export const cancelBill = (paymentId: string, reason: string) =>
  apiRequest<void>(`/mobile/v1/bills/payments/${encodeURIComponent(paymentId)}/cancel`, {
    method: "POST",
    body: JSON.stringify({ reason }),
  });
export const listBills = () => apiRequest<Bill[]>("/mobile/v1/bills");
export const issueBill = (payload: { amount: number; dueDate: string; description: string }) =>
  apiRequest<Bill>("/mobile/v1/bills", { method: "POST", body: JSON.stringify(payload) });

export const listTopUpOperators = () =>
  apiRequest<Array<{ id: string; name: string }>>("/mobile/v1/topups/operators");
export const listTopUpProducts = (operatorId: string) =>
  apiRequest<Array<{ id: string; name: string; amount: number }>>(
    `/mobile/v1/topups/operators/${encodeURIComponent(operatorId)}/products`,
  );
export const createTopUp = (
  payload: { operatorId: string; productId: string; phone: string; amount: number },
  idempotencyKey: string,
) =>
  apiRequest<MobileTopUp>("/mobile/v1/topups", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify(payload),
  });
export const getTopUpStatus = (id: string) =>
  apiRequest<MobileTopUp>(`/mobile/v1/topups/${encodeURIComponent(id)}`);
export const retryTopUp = (id: string) =>
  apiRequest<MobileTopUp>(`/mobile/v1/topups/${encodeURIComponent(id)}/retry`, {
    method: "POST",
    idempotencyKey: `topup-retry-${id}`,
  });

export const lookupVehicleDebts = (document: string, renavam: string) =>
  apiRequest<VehicleDebt[]>(
    `/mobile/v1/vehicles/debts?document=${encodeURIComponent(
      document,
    )}&renavam=${encodeURIComponent(renavam)}`,
  );
export const payVehicleDebts = (debtIds: string[], idempotencyKey: string) =>
  apiRequest<PaymentResponse>("/mobile/v1/vehicles/debts/payment", {
    method: "POST",
    idempotencyKey,
    body: JSON.stringify({ debtIds }),
  });

export function validateBillCode(value: string): boolean {
  const digits = value.replace(/\D/g, "");
  return (
    digits.length === 44 || digits.length === 46 || digits.length === 47 || digits.length === 48
  );
}

export function validateAmount(value: number): boolean {
  return Number.isFinite(value) && value > 0;
}

export async function openPaymentReceipt(url: string): Promise<void> {
  const parsed = new URL(url);
  if (parsed.protocol !== "https:") throw new Error("Receipt URL must use HTTPS");
  const { Linking } = await import("react-native");
  await Linking.openURL(parsed.toString());
}
