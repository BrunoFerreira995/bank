import { createContext, type PropsWithChildren } from "react";
import { View } from "react-native";

export function SafeAreaProvider({ children }: PropsWithChildren) {
  return <>{children}</>;
}
export function SafeAreaView({ children, ...props }: PropsWithChildren<Record<string, unknown>>) {
  return <View {...props}>{children}</View>;
}
export function useSafeAreaInsets() {
  return { top: 0, right: 0, bottom: 0, left: 0 };
}
export const initialWindowMetrics = null;
export const SafeAreaInsetsContext = createContext({ top: 0, right: 0, bottom: 0, left: 0 });
