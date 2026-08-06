import * as Keychain from "react-native-keychain";

export type BiometricAvailability = "AVAILABLE" | "UNAVAILABLE";

export async function getBiometricAvailability(): Promise<BiometricAvailability> {
  const type = await Keychain.getSupportedBiometryType();
  return type ? "AVAILABLE" : "UNAVAILABLE";
}

export async function saveBiometricPreference(enabled: boolean): Promise<void> {
  await Keychain.setGenericPassword("enabled", String(enabled), {
    service: "celcoin.mobile.biometric-preference",
    accessible: Keychain.ACCESSIBLE.WHEN_UNLOCKED_THIS_DEVICE_ONLY,
  });
}

export async function readBiometricPreference(): Promise<boolean> {
  const value = await Keychain.getGenericPassword({
    service: "celcoin.mobile.biometric-preference",
  });
  return value !== false && value?.password === "true";
}
