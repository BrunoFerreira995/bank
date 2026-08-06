import { View } from "react-native";

export function enableScreens() {
  return undefined;
}
export function enableFreeze() {
  return undefined;
}
export function screensEnabled() {
  return false;
}
export function ScreenContainer({
  children,
  ...props
}: {
  children?: React.ReactNode;
  [key: string]: unknown;
}) {
  return <View {...props}>{children}</View>;
}
export function Screen({
  children,
  ...props
}: {
  children?: React.ReactNode;
  [key: string]: unknown;
}) {
  return <View {...props}>{children}</View>;
}
