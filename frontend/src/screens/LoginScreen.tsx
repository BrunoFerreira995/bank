import { useState } from "react";
import { StyleSheet, View } from "react-native";
import { Button, Surface, Text, TextInput } from "react-native-paper";
import { useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { useSessionStore } from "@/core/auth/session-store";
import { login, recoverAccess, verifyMfa } from "@/core/identity/identity-api";

export function LoginScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<{ Onboarding: undefined }>>();
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [mfaCode, setMfaCode] = useState("");
  const [challengeId, setChallengeId] = useState<string | null>(null);
  const [recovery, setRecovery] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const setSession = useSessionStore((state) => state.setSession);

  async function submitLogin() {
    setLoading(true);
    setError(null);
    try {
      const response = challengeId
        ? await verifyMfa({ challengeId, code: mfaCode })
        : await login({ identifier, password });
      if (response.mfaRequired && response.challengeId) {
        setChallengeId(response.challengeId);
      } else if (response.accessToken) {
        await setSession({
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
          expiresAt: response.expiresAt,
        });
      } else {
        setError("Não foi possível autenticar. Tente novamente.");
      }
    } catch {
      setError(
        recovery
          ? "Não foi possível solicitar a recuperação."
          : "Não foi possível autenticar. Verifique os dados e tente novamente.",
      );
    } finally {
      setLoading(false);
    }
  }

  async function requestRecovery() {
    setLoading(true);
    setError(null);
    try {
      await recoverAccess(identifier);
      setError("Se os dados estiverem corretos, enviaremos as instruções de recuperação.");
    } catch {
      setError("Não foi possível solicitar a recuperação.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Surface elevation={1} style={styles.panel}>
        <Text variant="labelLarge" style={styles.eyebrow}>
          CELCOIN BANK
        </Text>
        <Text accessibilityRole="header" variant="headlineMedium">
          {recovery
            ? "Recupere seu acesso"
            : challengeId
            ? "Confirme sua identidade"
            : "Acesse sua conta"}
        </Text>
        <Text variant="bodyMedium" style={styles.description}>
          Use seus dados para acessar sua conta com segurança.
        </Text>
        {!challengeId ? (
          <TextInput
            testID="login-identifier"
            accessibilityLabel="CPF ou CNPJ"
            autoCapitalize="none"
            label="CPF ou CNPJ"
            mode="outlined"
            placeholder="CPF ou CNPJ"
            value={identifier}
            onChangeText={setIdentifier}
          />
        ) : null}
        {!challengeId && !recovery ? (
          <TextInput
            testID="login-password"
            accessibilityLabel="Senha"
            label="Senha"
            mode="outlined"
            placeholder="Senha"
            secureTextEntry
            value={password}
            onChangeText={setPassword}
          />
        ) : null}
        {challengeId ? (
          <TextInput
            testID="login-mfa-code"
            accessibilityLabel="Código de autenticação multifator"
            keyboardType="number-pad"
            label="Código MFA"
            mode="outlined"
            placeholder="Código MFA"
            value={mfaCode}
            onChangeText={setMfaCode}
          />
        ) : null}
        {error ? (
          <Text accessibilityRole="alert" style={styles.error}>
            {error}
          </Text>
        ) : null}
        <Button
          testID="login-submit"
          disabled={
            loading ||
            !identifier ||
            (!recovery && !challengeId && !password) ||
            (!!challengeId && !mfaCode)
          }
          loading={loading}
          mode="contained"
          onPress={() => (recovery ? requestRecovery() : submitLogin())}
        >
          {loading
            ? "Aguarde..."
            : recovery
            ? "Enviar instruções"
            : challengeId
            ? "Confirmar código"
            : "Entrar"}
        </Button>
        {!challengeId ? (
          <Button mode="text" onPress={() => setRecovery(!recovery)}>
            {recovery ? "Voltar para entrar" : "Esqueci minha senha"}
          </Button>
        ) : null}
        {!challengeId && !recovery ? (
          <Button mode="outlined" onPress={() => navigation.navigate("Onboarding")}>
            Abrir uma conta
          </Button>
        ) : null}
      </Surface>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { alignItems: "center", flex: 1, justifyContent: "center", padding: 24 },
  panel: { gap: 16, maxWidth: 520, padding: 32, width: "100%" },
  eyebrow: { color: "#006C5B", fontWeight: "700", letterSpacing: 1.2 },
  description: { color: "#3F4946", marginBottom: 8 },
  error: { color: "#BA1A1A" },
});
