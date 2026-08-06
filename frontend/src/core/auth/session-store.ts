import { create } from "zustand";
import { clearSession, readSession, saveSession, type StoredSession } from "./secure-session";

type SessionState = {
  session: StoredSession | null;
  hydrated: boolean;
  setSession: (session: StoredSession) => Promise<void>;
  restore: () => Promise<void>;
  logout: () => Promise<void>;
};

export const useSessionStore = create<SessionState>((set) => ({
  session: null,
  hydrated: false,
  async setSession(session) {
    await saveSession(session);
    set({ session });
  },
  async restore() {
    set({ session: await readSession(), hydrated: true });
  },
  async logout() {
    await clearSession();
    set({ session: null });
  },
}));
