import { apiRequest } from "@/core/http/api-client";

export type Notification = {
  id: string;
  title: string;
  body: string;
  category: string;
  readAt?: string;
  createdAt: string;
};
export type Ticket = {
  id: string;
  subject: string;
  status: "OPEN" | "IN_PROGRESS" | "CLOSED";
  updatedAt: string;
};
export type Faq = { id: string; question: string; answer: string; category: string };
export type ServiceStatus = {
  service: string;
  status: "OPERATIONAL" | "DEGRADED" | "MAINTENANCE" | "OUTAGE";
  message?: string;
  updatedAt: string;
};

export const registerDeviceToken = (token: string, platform: "ANDROID" | "IOS") =>
  apiRequest<void>("/mobile/v1/devices", {
    method: "POST",
    body: JSON.stringify({ token, platform }),
  });
export const rotateDeviceToken = (token: string, platform: "ANDROID" | "IOS") =>
  apiRequest<void>("/mobile/v1/devices/rotate", {
    method: "POST",
    body: JSON.stringify({ token, platform }),
  });
export const listNotifications = () => apiRequest<Notification[]>("/mobile/v1/notifications");
export const markNotificationRead = (id: string) =>
  apiRequest<void>(`/mobile/v1/notifications/${encodeURIComponent(id)}/read`, { method: "POST" });
export const listFaqs = () => apiRequest<Faq[]>("/mobile/v1/support/faqs");
export const listTickets = () => apiRequest<Ticket[]>("/mobile/v1/support/tickets");
export const createTicket = (subject: string, description: string) =>
  apiRequest<Ticket>("/mobile/v1/support/tickets", {
    method: "POST",
    body: JSON.stringify({ subject, description }),
  });
export const getTicket = (id: string) =>
  apiRequest<Ticket>(`/mobile/v1/support/tickets/${encodeURIComponent(id)}`);
export const getServicesStatus = () =>
  apiRequest<ServiceStatus[]>("/mobile/v1/operations/services");

export function safeErrorMessage(error: unknown): string {
  if (error instanceof Error && error.message) return error.message;
  return "Não foi possível concluir a operação. Tente novamente.";
}
