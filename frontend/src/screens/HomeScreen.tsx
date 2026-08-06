import { Button, StyleSheet, Text, View } from "react-native";
import { useSessionStore } from "@/core/auth/session-store";
import { env } from "@/config/env";

export function HomeScreen() {
  const logout = useSessionStore((state) => state.logout);
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Minha conta
      </Text>
      <Text>Ambiente: {env.environment}</Text>
      <Text>Pix: {env.flags.pix ? "habilitado" : "indisponível"}</Text>
      <Button title="Sair" onPress={() => logout()} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
