import { StyleSheet, Text, View } from "react-native";

export function AccessibleStatus({
  title,
  message,
  tone = "info",
}: {
  title: string;
  message: string;
  tone?: "info" | "error" | "success";
}) {
  return (
    <View
      accessibilityLabel={`status-${title}`}
      accessibilityLiveRegion="polite"
      style={styles.container}
    >
      <Text accessibilityRole="header" style={styles.title}>
        {title}
      </Text>
      <Text accessibilityRole={tone === "error" ? "alert" : "text"}>{message}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { gap: 4, padding: 12 },
  title: { fontWeight: "700" },
});
