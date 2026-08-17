import { create } from "zustand";
import { revokeSession } from "@/core/identity/identity-api";
import {
  clearSession,
  isSessionExpired,
  readSession,
  saveSession,
  type StoredSession,
} from "./secure-session";

type SessionState = {
  session: StoredSession | null;
  hydrated: boolean;
  setSession: (session: StoredSession) => Promise<void>;
  restore: () => Promise<void>;
  logout: () => Promise<void>;
  expire: () => Promise<void>;
};

export const useSessionStore = create<SessionState>((set) => ({
  session: null,
  hydrated: false,
  async setSession(session) {
    if (!session.accessToken || isSessionExpired(session)) throw new Error("Invalid session");
    await saveSession(session);
    set({ session });
  },
  async restore() {
    try {
      set({ session: await readSession(), hydrated: true });
    } catch {
      await clearSession();
      set({ session: null, hydrated: true });
    }
  },
  async logout() {
    try {
      if (useSessionStore.getState().session) await revokeSession();
    } catch {
      // Local logout must succeed even when the BFF is unavailable.
    } finally {
      await clearSession();
      set({ session: null });
    }
  },
  async expire() {
    await clearSession();
    set({ session: null });
  },
}));
