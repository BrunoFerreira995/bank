import * as Keychain from "react-native-keychain";

const ACCESS_TOKEN_KEY = "celcoin.mobile.access-token";
const REFRESH_TOKEN_KEY = "celcoin.mobile.refresh-token";

export type StoredSession = {
  accessToken: string;
  refreshToken?: string;
  expiresAt?: number;
};

export function isSessionExpired(session: StoredSession, now = Date.now()): boolean {
  if (!session.expiresAt) return false;
  // BFFs commonly return epoch seconds while browsers/native APIs use ms.
  const expiresAt =
    session.expiresAt < 10_000_000_000 ? session.expiresAt * 1000 : session.expiresAt;
  return expiresAt <= now;
}

export async function readSession(): Promise<StoredSession | null> {
  const credentials = await Keychain.getGenericPassword({ service: ACCESS_TOKEN_KEY });
  if (!credentials) return null;
  try {
    const refresh = await Keychain.getGenericPassword({ service: REFRESH_TOKEN_KEY });
    const parsed = JSON.parse(credentials.password) as Partial<StoredSession>;
    if (typeof parsed.accessToken !== "string" || !parsed.accessToken) {
      await clearSession();
      return null;
    }
    const session = {
      ...parsed,
      refreshToken: refresh ? refresh.password : parsed.refreshToken,
    } as StoredSession;
    if (isSessionExpired(session)) {
      await clearSession();
      return null;
    }
    return session;
  } catch {
    await clearSession();
    return null;
  }
}

export async function saveSession(session: StoredSession): Promise<void> {
  await Keychain.setGenericPassword("session", JSON.stringify(session), {
    service: ACCESS_TOKEN_KEY,
    accessible: Keychain.ACCESSIBLE.WHEN_UNLOCKED_THIS_DEVICE_ONLY,
  });
  if (session.refreshToken) {
    await Keychain.setGenericPassword("refresh", session.refreshToken, {
      service: REFRESH_TOKEN_KEY,
      accessible: Keychain.ACCESSIBLE.WHEN_UNLOCKED_THIS_DEVICE_ONLY,
    });
  } else {
    await Keychain.resetGenericPassword({ service: REFRESH_TOKEN_KEY });
  }
}

export async function clearSession(): Promise<void> {
  await Promise.all([
    Keychain.resetGenericPassword({ service: ACCESS_TOKEN_KEY }),
    Keychain.resetGenericPassword({ service: REFRESH_TOKEN_KEY }),
  ]);
}
