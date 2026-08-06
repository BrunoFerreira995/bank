import { NativeModules } from "react-native";

type PasskeyModule = {
  register: (challenge: string) => Promise<string>;
  authenticate: (challenge: string) => Promise<string>;
};
const passkey = NativeModules.CelcoinPasskey as PasskeyModule | undefined;

export async function registerPasskey(challenge: string): Promise<string> {
  if (!passkey?.register) throw new Error("Passkey indisponível neste dispositivo");
  return passkey.register(challenge);
}

export async function authenticatePasskey(challenge: string): Promise<string> {
  if (!passkey?.authenticate) throw new Error("Passkey indisponível neste dispositivo");
  return passkey.authenticate(challenge);
}

export function canUsePasskey(): boolean {
  return !!passkey?.register && !!passkey?.authenticate;
}
