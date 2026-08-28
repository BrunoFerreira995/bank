import { DefaultTheme as NavigationDefaultTheme } from "@react-navigation/native";
import { MD3LightTheme, adaptNavigationTheme, type MD3Theme } from "react-native-paper";

export const appTheme: MD3Theme = {
  ...MD3LightTheme,
  roundness: 14,
  colors: {
    ...MD3LightTheme.colors,
    primary: "#006C5B",
    onPrimary: "#FFFFFF",
    primaryContainer: "#79F8D7",
    onPrimaryContainer: "#00201A",
    secondary: "#426277",
    onSecondary: "#FFFFFF",
    secondaryContainer: "#C6E7FF",
    onSecondaryContainer: "#001E2D",
    tertiary: "#545D7E",
    background: "#F5F8F7",
    onBackground: "#171D1B",
    surface: "#FFFFFF",
    onSurface: "#171D1B",
    surfaceVariant: "#DBE5E1",
    onSurfaceVariant: "#3F4946",
    outline: "#6F7975",
    error: "#BA1A1A",
  },
};

const { LightTheme } = adaptNavigationTheme({
  reactNavigationLight: NavigationDefaultTheme,
});

export const navigationTheme = {
  ...LightTheme,
  fonts: NavigationDefaultTheme.fonts,
  colors: {
    ...LightTheme.colors,
    primary: appTheme.colors.primary,
    background: appTheme.colors.background,
    card: appTheme.colors.surface,
    text: appTheme.colors.onSurface,
    border: appTheme.colors.outlineVariant,
  },
};
