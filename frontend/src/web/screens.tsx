import type { ComponentProps } from "react";
import { View } from "react-native";

type ScreenProps = ComponentProps<typeof View>;

function WebScreen({
  children,
  pointerEvents,
  style,
  ...props
}: ScreenProps) {
  return (
    <View
      {...props}
      collapsable={false}
      style={[
        style,
        pointerEvents ? { pointerEvents } : undefined,
      ]}
    >
      {children}
    </View>
  );
}

export function enableScreens(): void {}
export function enableFreeze(): void {}

export function screensEnabled(): boolean {
  return false;
}

export function ScreenContainer(props: ScreenProps) {
  return <WebScreen {...props} />;
}

export function Screen(props: ScreenProps) {
  return <WebScreen {...props} />;
}