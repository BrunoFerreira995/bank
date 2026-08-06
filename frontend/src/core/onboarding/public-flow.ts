import { Linking } from "react-native";

export async function openPublicOnboarding(url: string): Promise<void> {
  const parsed = new URL(url);
  if (!/^https:$/.test(parsed.protocol)) throw new Error("Onboarding URL must use HTTPS");
  if (!(await Linking.canOpenURL(parsed.toString())))
    throw new Error("Onboarding URL cannot be opened");
  await Linking.openURL(parsed.toString());
}
