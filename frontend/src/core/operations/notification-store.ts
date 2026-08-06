import { create } from "zustand";
import type { Notification } from "./operations-api";

type NotificationState = {
  items: Notification[];
  setItems: (items: Notification[]) => void;
  markRead: (id: string) => void;
};
export const useNotificationStore = create<NotificationState>((set) => ({
  items: [],
  setItems: (items) => set({ items }),
  markRead: (id) =>
    set((state) => ({
      items: state.items.map((item) =>
        item.id === id ? { ...item, readAt: new Date().toISOString() } : item,
      ),
    })),
}));
