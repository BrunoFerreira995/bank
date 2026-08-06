import { getServicesStatus, listNotifications } from "./operations-api";
import { useNotificationStore } from "./notification-store";

export type PushEvent = {
  type: "NOTIFICATION_REFRESH" | "SERVICE_STATUS_REFRESH";
  reference?: string;
};

export async function processPushEvent(event: PushEvent): Promise<void> {
  // Push is only a hint. Financial state is never trusted from a notification payload.
  if (event.type === "NOTIFICATION_REFRESH")
    useNotificationStore.getState().setItems(await listNotifications());
  if (event.type === "SERVICE_STATUS_REFRESH") await getServicesStatus();
}
