import { apiRequest } from "@/core/http/api-client";

export type PixPayment = {
  id: string;
  status: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED";
  amount: number;
  endToEndId?: string;
  createdAt: string;
};
export type PixParty = {
  name: string;
  document?: string;
  bankCode?: string;
  branch?: string;
  account?: string;
};
export type PixPaymentRequest = {
  accountId: string;
  amount: number;
  description?: string;
  key?: string;
  qrCode?: string;
  beneficiary?: PixParty;
};
export type PixCharge = {
  id: string;
  type: "IMMEDIATE" | "DUEDATE" | "STATIC";
  brCode: string;
  amount?: number;
  status: string;
};
export type PixKey = {
  key: string;
  type: "CPF" | "CNPJ" | "EMAIL" | "PHONE" | "RANDOM";
  name?: string;
  status: string;
};
export type PixOperation = { id: string; status: string; createdAt: string };
export type PixSchedule = { id: string; status: string; amount: number; nextDate?: string };

export const payByKey = (request: PixPaymentRequest) =>
  apiRequest<PixPayment>("/mobile/v1/pix/payments/key", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const payByBankDetails = (request: PixPaymentRequest) =>
  apiRequest<PixPayment>("/mobile/v1/pix/payments/bank-details", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const payStaticQr = (request: PixPaymentRequest) =>
  apiRequest<PixPayment>("/mobile/v1/pix/payments/qr/static", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const payDynamicQr = (request: PixPaymentRequest) =>
  apiRequest<PixPayment>("/mobile/v1/pix/payments/qr/dynamic", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const createImmediateCharge = (payload: Omit<PixCharge, "id" | "brCode" | "status">) =>
  apiRequest<PixCharge>("/mobile/v1/pix/charges/immediate", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const createDueDateCharge = (
  payload: Omit<PixCharge, "id" | "brCode" | "status"> & { dueDate: string },
) =>
  apiRequest<PixCharge>("/mobile/v1/pix/charges/duedate", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const createStaticCharge = (payload: Omit<PixCharge, "id" | "brCode" | "status">) =>
  apiRequest<PixCharge>("/mobile/v1/pix/charges/static", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const getPixPayments = () => apiRequest<PixPayment[]>("/mobile/v1/pix/payments");
export const getPixReceipts = () => apiRequest<PixPayment[]>("/mobile/v1/pix/receipts");
export const refundPix = (paymentId: string, amount?: number) =>
  apiRequest<PixOperation>(`/mobile/v1/pix/payments/${encodeURIComponent(paymentId)}/refund`, {
    method: "POST",
    body: JSON.stringify({ amount }),
  });
export const getPixCautionBlocks = () =>
  apiRequest<PixOperation[]>("/mobile/v1/pix/cautionary-blocks");
export const listPixKeys = (accountId: string) =>
  apiRequest<PixKey[]>(`/mobile/v1/pix/keys?accountId=${encodeURIComponent(accountId)}`);
export const createPixKey = (accountId: string, key: Omit<PixKey, "status">) =>
  apiRequest<PixKey>("/mobile/v1/pix/keys", {
    method: "POST",
    body: JSON.stringify({ accountId, ...key }),
  });
export const updatePixKeyName = (key: string, name: string) =>
  apiRequest<PixKey>(`/mobile/v1/pix/keys/${encodeURIComponent(key)}`, {
    method: "PATCH",
    body: JSON.stringify({ name }),
  });
export const deletePixKey = (key: string) =>
  apiRequest<void>(`/mobile/v1/pix/keys/${encodeURIComponent(key)}`, { method: "DELETE" });
export const requestKeyPortability = (key: string) =>
  apiRequest<PixOperation>("/mobile/v1/pix/key-portability", {
    method: "POST",
    body: JSON.stringify({ key }),
  });
export const requestKeyClaim = (key: string) =>
  apiRequest<PixOperation>("/mobile/v1/pix/key-claims", {
    method: "POST",
    body: JSON.stringify({ key }),
  });
export const getKeyOperations = () => apiRequest<PixOperation[]>("/mobile/v1/pix/key-operations");
export const getPixAutomatic = () => apiRequest<PixSchedule[]>("/mobile/v1/pix/automatic");
export const getPixIndirect = () => apiRequest<PixOperation[]>("/mobile/v1/pix/indirect");
export const getPixSmart = () => apiRequest<PixOperation[]>("/mobile/v1/pix/smart");
export const createPixWithdrawal = (payload: PixPaymentRequest & { change?: number }) =>
  apiRequest<PixOperation>("/mobile/v1/pix/withdrawals", {
    method: "POST",
    body: JSON.stringify(payload),
  });
export const getPixReceipt = (paymentId: string) =>
  apiRequest<{ downloadUrl: string }>(
    `/mobile/v1/pix/payments/${encodeURIComponent(paymentId)}/receipt`,
  );

export async function openPixReceipt(paymentId: string): Promise<void> {
  const { downloadUrl } = await getPixReceipt(paymentId);
  const url = new URL(downloadUrl);
  if (url.protocol !== "https:") throw new Error("Receipt URL must use HTTPS");
  const { Linking } = await import("react-native");
  await Linking.openURL(url.toString());
}
