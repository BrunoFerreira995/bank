import { Linking } from "react-native";

export async function openOpenFinanceRedirect(url: string): Promise<void> {
  const parsed = new URL(url);
  if (parsed.protocol !== "https:") throw new Error("Open Finance redirect must use HTTPS");
  if (!(await Linking.canOpenURL(parsed.toString())))
    throw new Error("Open Finance redirect unavailable");
  await Linking.openURL(parsed.toString());
}
