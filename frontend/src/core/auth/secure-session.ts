import * as Keychain from "react-native-keychain";

const ACCESS_TOKEN_KEY = "celcoin.mobile.access-token";
const REFRESH_TOKEN_KEY = "celcoin.mobile.refresh-token";

export type StoredSession = {
  accessToken: string;
  refreshToken?: string;
  expiresAt?: number;
};

export async function readSession(): Promise<StoredSession | null> {
  const credentials = await Keychain.getGenericPassword({ service: ACCESS_TOKEN_KEY });
  if (!credentials) return null;
  const refresh = await Keychain.getGenericPassword({ service: REFRESH_TOKEN_KEY });
  const parsed = JSON.parse(credentials.password) as StoredSession;
  return { ...parsed, refreshToken: refresh ? refresh.password : parsed.refreshToken };
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
