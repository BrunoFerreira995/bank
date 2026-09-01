import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet } from "react-native";
import { Button, Card, Text, TextInput } from "react-native-paper";
import { getProfile, updateProfile } from "@/core/account/account-api";
import { changePassword } from "@/core/identity/identity-api";
import { sharedStyles } from "@/ui/layout";

export function ProfileScreen() {
  const profile = useQuery({ queryKey: ["profile"], queryFn: getProfile });
  const [email, setEmail] = useState(""); const [currentPassword, setCurrentPassword] = useState(""); const [newPassword, setNewPassword] = useState(""); const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [profileMessage, setProfileMessage] = useState<string | null>(null); const [passwordMessage, setPasswordMessage] = useState<string | null>(null); const [changing, setChanging] = useState(false);
  useEffect(() => { if (profile.data?.email) setEmail(profile.data.email); }, [profile.data?.email]);
  async function save() { try { await updateProfile({ email }); await profile.refetch(); setProfileMessage("Dados cadastrais atualizados."); } catch { setProfileMessage("Não foi possível atualizar os dados cadastrais."); } }
  async function savePassword() { setPasswordMessage(null); if (newPassword.length < 8 || newPassword !== passwordConfirmation) return setPasswordMessage("A nova senha precisa ter ao menos 8 caracteres e coincidir com a confirmação."); setChanging(true); try { await changePassword(currentPassword, newPassword); setCurrentPassword(""); setNewPassword(""); setPasswordConfirmation(""); setPasswordMessage("Senha alterada com sucesso."); } catch { setPasswordMessage("Não foi possível alterar a senha. Verifique os dados e tente novamente."); } finally { setChanging(false); } }
  return <ScrollView contentContainerStyle={sharedStyles.page}>
    <Text accessibilityRole="header" style={sharedStyles.title}>Perfil</Text>
    <Card mode="outlined" style={sharedStyles.card}><Card.Title title="Dados cadastrais" subtitle={profile.isLoading ? "Carregando dados..." : profile.data?.name} /><Card.Content style={styles.form}>
      {profile.data?.document ? <Text style={sharedStyles.subtitle}>{profile.data.document}</Text> : null}
      <TextInput accessibilityLabel="E-mail" label="E-mail" mode="outlined" autoCapitalize="none" keyboardType="email-address" value={email} onChangeText={setEmail} />
      <Button mode="contained" disabled={!email} onPress={save}>Salvar alterações</Button>
      {profileMessage ? <Text accessibilityRole="alert" style={profileMessage.includes("atualizados") ? sharedStyles.message : sharedStyles.error}>{profileMessage}</Text> : null}
    </Card.Content></Card>
    <Card mode="outlined" style={sharedStyles.card}><Card.Title title="Alterar senha" subtitle="Use uma senha com pelo menos 8 caracteres." /><Card.Content style={styles.form}>
      <TextInput accessibilityLabel="Senha atual" label="Senha atual" mode="outlined" secureTextEntry value={currentPassword} onChangeText={setCurrentPassword} />
      <TextInput accessibilityLabel="Nova senha" label="Nova senha" mode="outlined" secureTextEntry value={newPassword} onChangeText={setNewPassword} error={!!passwordMessage && !passwordMessage.includes("sucesso")} />
      <TextInput accessibilityLabel="Confirmar nova senha" label="Confirmar nova senha" mode="outlined" secureTextEntry value={passwordConfirmation} onChangeText={setPasswordConfirmation} error={!!passwordMessage && !passwordMessage.includes("sucesso")} />
      <Button mode="contained" loading={changing} disabled={changing || !currentPassword || !newPassword || !passwordConfirmation} onPress={savePassword}>Alterar senha</Button>
      {passwordMessage ? <Text accessibilityRole="alert" style={passwordMessage.includes("sucesso") ? sharedStyles.message : sharedStyles.error}>{passwordMessage}</Text> : null}
    </Card.Content></Card>
  </ScrollView>;
}
const styles = StyleSheet.create({ form: { gap: 14 } });
