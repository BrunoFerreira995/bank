import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
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
      <Text accessibilityRole="header" style={styles.title}>
        {recovery
          ? "Recupere seu acesso"
          : challengeId
          ? "Confirme sua identidade"
          : "Acesse sua conta"}
      </Text>
      {!challengeId ? (
        <TextInput
          accessibilityLabel="CPF ou CNPJ"
          autoCapitalize="none"
          placeholder="CPF ou CNPJ"
          value={identifier}
          onChangeText={setIdentifier}
        />
      ) : null}
      {!challengeId && !recovery ? (
        <TextInput
          accessibilityLabel="Senha"
          placeholder="Senha"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
        />
      ) : null}
      {challengeId ? (
        <TextInput
          accessibilityLabel="Código de autenticação multifator"
          keyboardType="number-pad"
          placeholder="Código MFA"
          value={mfaCode}
          onChangeText={setMfaCode}
        />
      ) : null}
      {error ? <Text accessibilityRole="alert">{error}</Text> : null}
      <Button
        disabled={
          loading ||
          !identifier ||
          (!recovery && !challengeId && !password) ||
          (!!challengeId && !mfaCode)
        }
        title={
          loading
            ? "Aguarde..."
            : recovery
            ? "Enviar instruções"
            : challengeId
            ? "Confirmar código"
            : "Entrar"
        }
        onPress={() => (recovery ? requestRecovery() : submitLogin())}
      />
      {!challengeId ? (
        <Button
          title={recovery ? "Voltar para entrar" : "Esqueci minha senha"}
          onPress={() => setRecovery(!recovery)}
        />
      ) : null}
      {!challengeId && !recovery ? (
        <Button title="Abrir uma conta" onPress={() => navigation.navigate("Onboarding")} />
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
