import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { apiRequest } from "@/core/http/api-client";
import { useSessionStore } from "@/core/auth/session-store";

type LoginResponse = { accessToken: string; refreshToken?: string };

export function LoginScreen() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const setSession = useSessionStore((state) => state.setSession);

  async function login() {
    setLoading(true);
    setError(null);
    try {
      const response = await apiRequest<LoginResponse>("/mobile/v1/session", {
        method: "POST",
        body: JSON.stringify({ identifier, password }),
      });
      await setSession(response);
    } catch {
      setError("Não foi possível entrar. Verifique os dados e tente novamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Acesse sua conta
      </Text>
      <TextInput
        accessibilityLabel="CPF ou CNPJ"
        autoCapitalize="none"
        placeholder="CPF ou CNPJ"
        value={identifier}
        onChangeText={setIdentifier}
      />
      <TextInput
        accessibilityLabel="Senha"
        placeholder="Senha"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />
      {error ? <Text accessibilityRole="alert">{error}</Text> : null}
      <Button
        disabled={loading || !identifier || !password}
        title={loading ? "Entrando..." : "Entrar"}
        onPress={() => login()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
