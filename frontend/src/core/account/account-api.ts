import { apiRequest } from "@/core/http/api-client";

export type AccountStatus = "ACTIVE" | "BLOCKED" | "CLOSED" | "PENDING" | "UNDER_REVIEW";
export type Account = {
  id: string;
  name: string;
  branch: string;
  number: string;
  status: AccountStatus;
};
export type Balance = { available: number; blocked: number; currency: "BRL"; asOf: string };
export type Transaction = {
  id: string;
  description: string;
  amount: number;
  direction: "CREDIT" | "DEBIT";
  type: string;
  status: "PENDING" | "COMPLETED" | "FAILED" | "REVERSED";
  occurredAt: string;
};
export type Page<T> = {
  items: T[];
  page: number;
  size: number;
  totalPages: number;
  totalItems: number;
};
export type StatementFilters = {
  from?: string;
  to?: string;
  type?: string;
  status?: Transaction["status"];
};
export type Profile = {
  name: string;
  document: string;
  email: string;
  phone: string;
  address: Record<string, string>;
};
export type JudicialBlock = {
  id: string;
  amount: number;
  status: "ACTIVE" | "RELEASED";
  reason?: string;
  createdAt: string;
};
export type TransferRequest = {
  accountId: string;
  amount: number;
  destination: { bankCode: string; branch: string; number: string; document: string };
  description?: string;
};
export type TransferResponse = { id: string; status: "PENDING" | "COMPLETED" | "FAILED" };

export const listAccounts = () => apiRequest<Account[]>("/mobile/v1/accounts");
export const getBalance = (accountId: string) =>
  apiRequest<Balance>(`/mobile/v1/accounts/${encodeURIComponent(accountId)}/balance`);
export const getDailyMovements = (accountId: string) =>
  apiRequest<Transaction[]>(`/mobile/v1/accounts/${encodeURIComponent(accountId)}/movements/today`);
export const getStatement = (accountId: string, page: number, filters: StatementFilters = {}) =>
  apiRequest<Page<Transaction>>(
    `/mobile/v1/accounts/${encodeURIComponent(
      accountId,
    )}/statement?page=${page}&size=20&${new URLSearchParams(
      filters as Record<string, string>,
    ).toString()}`,
  );
export const getTransaction = (accountId: string, transactionId: string) =>
  apiRequest<Transaction>(
    `/mobile/v1/accounts/${encodeURIComponent(accountId)}/transactions/${encodeURIComponent(
      transactionId,
    )}`,
  );
export const getProfile = () => apiRequest<Profile>("/mobile/v1/profile");
export const updateProfile = (profile: Partial<Profile>) =>
  apiRequest<Profile>("/mobile/v1/profile", { method: "PATCH", body: JSON.stringify(profile) });
export const closeAccount = (accountId: string, reason: string) =>
  apiRequest<void>(`/mobile/v1/accounts/${encodeURIComponent(accountId)}/close`, {
    method: "POST",
    body: JSON.stringify({ reason }),
  });
export const getJudicialBlocks = (accountId: string) =>
  apiRequest<JudicialBlock[]>(
    `/mobile/v1/accounts/${encodeURIComponent(accountId)}/judicial-blocks`,
  );
export const getIncomeReport = (year: number) =>
  apiRequest<{ downloadUrl: string }>(`/mobile/v1/reports/income/${year}`);
export const getTransferStatus = (transferId: string) =>
  apiRequest<{ id: string; status: string }>(
    `/mobile/v1/transfers/${encodeURIComponent(transferId)}`,
  );
export const createInternalTransfer = (request: TransferRequest) =>
  apiRequest<TransferResponse>("/mobile/v1/transfers/internal", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const createTed = (request: TransferRequest) =>
  apiRequest<TransferResponse>("/mobile/v1/transfers/ted", {
    method: "POST",
    body: JSON.stringify(request),
  });
export const getReceipt = (transactionId: string) =>
  apiRequest<{ downloadUrl: string }>(
    `/mobile/v1/transactions/${encodeURIComponent(transactionId)}/receipt`,
  );

export async function openReceipt(transactionId: string): Promise<void> {
  const { downloadUrl } = await getReceipt(transactionId);
  const url = new URL(downloadUrl);
  if (url.protocol !== "https:") throw new Error("Receipt URL must use HTTPS");
  const { Linking } = await import("react-native");
  await Linking.openURL(url.toString());
}

export async function openIncomeReport(year: number): Promise<void> {
  const { downloadUrl } = await getIncomeReport(year);
  const url = new URL(downloadUrl);
  if (url.protocol !== "https:") throw new Error("Report URL must use HTTPS");
  const { Linking } = await import("react-native");
  await Linking.openURL(url.toString());
}
