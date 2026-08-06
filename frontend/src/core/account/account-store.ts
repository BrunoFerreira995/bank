import { create } from "zustand";

type ActiveAccountState = { accountId: string | null; setAccount: (accountId: string) => void };
export const useActiveAccount = create<ActiveAccountState>((set) => ({
  accountId: null,
  setAccount: (accountId) => set({ accountId }),
}));
