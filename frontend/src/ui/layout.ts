import { StyleSheet, useWindowDimensions } from "react-native";

export const breakpoints = { phone: 600, tablet: 1024, wide: 1440 } as const;
export const layout = {
  contentMaxWidth: 1240,
  pagePadding: 16,
  desktopPadding: 32,
  gap: 16,
  cardRadius: 16,
  touchTarget: 44,
} as const;

export function useResponsiveLayout() {
  const { width } = useWindowDimensions();
  return {
    width,
    isPhone: width < breakpoints.phone,
    isTablet: width >= breakpoints.phone && width < breakpoints.tablet,
    isDesktop: width >= breakpoints.tablet,
    pagePadding: width < breakpoints.phone ? layout.pagePadding : width < breakpoints.tablet ? 24 : layout.desktopPadding,
  };
}

export const sharedStyles = StyleSheet.create({
  page: { alignSelf: "center", gap: layout.gap, maxWidth: layout.contentMaxWidth, padding: layout.pagePadding, width: "100%" },
  title: { color: "#17211E", fontSize: 28, fontWeight: "700" },
  subtitle: { color: "#46534E", fontSize: 16, lineHeight: 23 },
  sectionTitle: { color: "#17211E", fontSize: 20, fontWeight: "700" },
  card: { borderRadius: layout.cardRadius },
  form: { gap: 12 },
  row: { flexDirection: "row", flexWrap: "wrap", gap: 12 },
  field: { flexGrow: 1, minWidth: 0 },
  message: { color: "#006C5B", fontSize: 14, lineHeight: 20 },
  error: { color: "#BA1A1A", fontSize: 14, lineHeight: 20 },
});
