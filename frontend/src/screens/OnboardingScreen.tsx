import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { createRegistration } from "@/core/onboarding/registration-api";
import {
  validateBrazilianPhone,
  validateCnpj,
  validateCpf,
  validateEmail,
} from "@/core/identity/identity-validation";

export function OnboardingScreen() {
  const [personType, setPersonType] = useState<"PF" | "PJ">("PF");
  const [document, setDocument] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [postalCode, setPostalCode] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit() {
    const validDocument = personType === "PF" ? validateCpf(document) : validateCnpj(document);
    if (
      !validDocument ||
      !validateEmail(email) ||
      !validateBrazilianPhone(phone) ||
      postalCode.replace(/\D/g, "").length !== 8
    ) {
      setMessage("Confira documento, contato e CEP antes de continuar.");
      return;
    }
    setLoading(true);
    setMessage(null);
    try {
      const response = await createRegistration({
        personType,
        document,
        email,
        phone,
        address: { postalCode, street: "", number: "", city: "", state: "" },
      });
      setMessage(`Cadastro iniciado: ${response.onboardingId}`);
    } catch {
      setMessage("Não foi possível iniciar o cadastro.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Abra sua conta
      </Text>
      <View style={styles.row}>
        <Button title="Pessoa física" onPress={() => setPersonType("PF")} />
        <Button title="Pessoa jurídica" onPress={() => setPersonType("PJ")} />
      </View>
      <TextInput
        accessibilityLabel={personType === "PF" ? "CPF" : "CNPJ"}
        placeholder={personType === "PF" ? "CPF" : "CNPJ"}
        value={document}
        onChangeText={setDocument}
      />
      <TextInput
        accessibilityLabel="E-mail"
        autoCapitalize="none"
        keyboardType="email-address"
        placeholder="E-mail"
        value={email}
        onChangeText={setEmail}
      />
      <TextInput
        accessibilityLabel="Telefone"
        keyboardType="phone-pad"
        placeholder="Telefone"
        value={phone}
        onChangeText={setPhone}
      />
      <TextInput
        accessibilityLabel="CEP"
        keyboardType="number-pad"
        placeholder="CEP"
        value={postalCode}
        onChangeText={setPostalCode}
      />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
      <Button disabled={loading} title={loading ? "Enviando..." : "Continuar"} onPress={submit} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  row: { flexDirection: "row", justifyContent: "space-between" },
  title: { fontSize: 28, fontWeight: "700" },
});
