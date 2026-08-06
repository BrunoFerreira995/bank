import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { getProfile, updateProfile } from "@/core/account/account-api";

export function ProfileScreen() {
  const profile = useQuery({ queryKey: ["profile"], queryFn: getProfile });
  const [email, setEmail] = useState("");
  async function save() {
    await updateProfile({ email });
    profile.refetch();
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
    </View>
  );
}

const styles = StyleSheet.create({
  container: { gap: 16, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
