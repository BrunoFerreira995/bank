import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { getProfile, updateProfile } from "@/core/account/account-api";
import { changePassword } from "@/core/identity/identity-api";

export function ProfileScreen() {
  const profile = useQuery({ queryKey: ["profile"], queryFn: getProfile });
  const [email, setEmail] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [passwordMessage, setPasswordMessage] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [changingPassword, setChangingPassword] = useState(false);
  const [profileMessage, setProfileMessage] = useState<string | null>(null);
  async function save() {
    setProfileMessage(null);
    try {
      await updateProfile({ email });
      await profile.refetch();
      setProfileMessage("Dados cadastrais atualizados.");
    } catch {
      setProfileMessage("Não foi possível atualizar os dados cadastrais.");
    }
  }
  async function savePassword() {
    setPasswordMessage(null);
    setPasswordError(null);
    if (newPassword.length < 8 || newPassword !== passwordConfirmation) {
      setPasswordError("Confira a nova senha e sua confirmação.");
      return;
    }
    setChangingPassword(true);
    try {
      await changePassword(currentPassword, newPassword);
      setCurrentPassword("");
      setNewPassword("");
      setPasswordConfirmation("");
      setPasswordMessage("Senha alterada com sucesso.");
    } catch {
      setPasswordError("Não foi possível alterar a senha. Verifique os dados e tente novamente.");
    } finally {
      setChangingPassword(false);
    }
  }
  if (profile.isLoading) return <Text>Carregando perfil...</Text>;
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Dados cadastrais
      </Text>
      <Text>{profile.data?.name}</Text>
      <Text>{profile.data?.document}</Text>
      <TextInput
        autoCapitalize="none"
        placeholder={profile.data?.email ?? "E-mail"}
        value={email}
        onChangeText={setEmail}
      />
      <Button title="Salvar alterações" disabled={!email} onPress={() => save()} />
      {profileMessage ? <Text accessibilityRole="alert">{profileMessage}</Text> : null}
      <Text accessibilityRole="header" style={styles.subtitle}>
        Trocar senha
      </Text>
      <TextInput
        accessibilityLabel="Senha atual"
        placeholder="Senha atual"
        secureTextEntry
        value={currentPassword}
        onChangeText={setCurrentPassword}
      />
      <TextInput
        accessibilityLabel="Nova senha"
        placeholder="Nova senha"
        secureTextEntry
        value={newPassword}
        onChangeText={setNewPassword}
      />
      <TextInput
        accessibilityLabel="Confirmar nova senha"
        placeholder="Confirmar nova senha"
        secureTextEntry
        value={passwordConfirmation}
        onChangeText={setPasswordConfirmation}
      />
      {passwordError ? <Text accessibilityRole="alert">{passwordError}</Text> : null}
      {passwordMessage ? <Text accessibilityLiveRegion="polite">{passwordMessage}</Text> : null}
      <Button
        title={changingPassword ? "Alterando..." : "Alterar senha"}
        disabled={changingPassword || !currentPassword || !newPassword || !passwordConfirmation}
        onPress={() => savePassword()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { gap: 16, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 12 },
});
