import { useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";
import { Button, Card, SegmentedButtons, Text, TextInput } from "react-native-paper";
import { createRegistration } from "@/core/onboarding/registration-api";
import { acceptConsents } from "@/core/identity/consents";
import { validateBrazilianPhone, validateCnpj, validateCpf, validateEmail } from "@/core/identity/identity-validation";
import { sharedStyles } from "@/ui/layout";

export function OnboardingScreen() {
  const [personType, setPersonType] = useState<"PF" | "PJ">("PF");
  const [document, setDocument] = useState(""); const [email, setEmail] = useState(""); const [phone, setPhone] = useState("");
  const [postalCode, setPostalCode] = useState(""); const [street, setStreet] = useState(""); const [number, setNumber] = useState(""); const [city, setCity] = useState(""); const [state, setState] = useState("");
  const [representativeName, setRepresentativeName] = useState(""); const [representativeDocument, setRepresentativeDocument] = useState("");
  const [consentsAccepted, setConsentsAccepted] = useState(false); const [message, setMessage] = useState<string | null>(null); const [loading, setLoading] = useState(false);
  async function submit() {
    const validDocument = personType === "PF" ? validateCpf(document) : validateCnpj(document);
    if (!validDocument || !validateEmail(email) || !validateBrazilianPhone(phone) || postalCode.replace(/\D/g, "").length !== 8 || !street.trim() || !number.trim() || !city.trim() || state.trim().length !== 2 || !consentsAccepted || (personType === "PJ" && (!representativeName.trim() || !validateCpf(representativeDocument)))) return setMessage("Preencha os campos obrigatórios e aceite os termos para continuar.");
    setLoading(true); setMessage(null);
    try {
      const response = await createRegistration({ personType, document, email, phone, address: { postalCode, street, number, city, state: state.toUpperCase() }, representatives: personType === "PJ" ? [{ name: representativeName, document: representativeDocument }] : undefined });
      await acceptConsents([{ type: "TERMS_OF_USE", version: "2026-01", accepted: true }, { type: "PRIVACY_POLICY", version: "2026-01", accepted: true }]);
      setMessage(response?.onboardingId ? `Cadastro iniciado: ${response.onboardingId}` : "Cadastro recebido, mas sem identificador retornado.");
    } catch { setMessage("Não foi possível iniciar o cadastro."); } finally { setLoading(false); }
  }
  const input = (label: string, value: string, change: (v: string) => void, options: Record<string, unknown> = {}) => <TextInput accessibilityLabel={label.replace(" *", "")} label={label} mode="outlined" value={value} onChangeText={change} {...options} />;
  return <ScrollView contentContainerStyle={styles.container}>
    <Text accessibilityRole="header" style={sharedStyles.title}>Abra sua conta</Text>
    <Text style={sharedStyles.subtitle}>Etapa 1 de 1 · campos com * são obrigatórios.</Text>
    <Card mode="outlined"><Card.Content style={styles.form}>
      <Text variant="titleMedium">Tipo de pessoa</Text>
      <SegmentedButtons value={personType} onValueChange={(v) => setPersonType(v as "PF" | "PJ")} buttons={[{ value: "PF", label: "Pessoa física" }, { value: "PJ", label: "Pessoa jurídica" }]} />
      {input(personType === "PF" ? "CPF *" : "CNPJ *", document, setDocument, { keyboardType: "number-pad", placeholder: personType === "PF" ? "000.000.000-00" : "00.000.000/0000-00" })}
      {input("E-mail *", email, setEmail, { autoCapitalize: "none", keyboardType: "email-address", placeholder: "voce@exemplo.com" })}
      {input("Telefone *", phone, setPhone, { keyboardType: "phone-pad", placeholder: "(00) 00000-0000" })}
      {input("CEP *", postalCode, setPostalCode, { keyboardType: "number-pad", placeholder: "00000-000" })}
      <View style={styles.row}><View style={styles.field}>{input("Rua *", street, setStreet)}</View><View style={styles.number}>{input("Número *", number, setNumber, { keyboardType: "number-pad" })}</View></View>
      <View style={styles.row}><View style={styles.field}>{input("Cidade *", city, setCity)}</View><View style={styles.state}>{input("UF *", state, setState, { autoCapitalize: "characters", maxLength: 2 })}</View></View>
      {personType === "PJ" ? <><Text variant="titleMedium">Representante legal</Text>{input("Nome do representante *", representativeName, setRepresentativeName)}{input("CPF do representante *", representativeDocument, setRepresentativeDocument, { keyboardType: "number-pad" })}</> : null}
      <Button mode={consentsAccepted ? "contained-tonal" : "outlined"} icon={consentsAccepted ? "✓" : undefined} onPress={() => setConsentsAccepted((value) => !value)}>{consentsAccepted ? "Termos e privacidade aceitos" : "Aceitar termos e privacidade"}</Button>
      {message ? <Text accessibilityRole="alert" style={message.startsWith("Cadastro iniciado") ? sharedStyles.message : sharedStyles.error}>{message}</Text> : null}
      <Button mode="contained" loading={loading} disabled={loading} onPress={submit}>Continuar</Button>
    </Card.Content></Card>
  </ScrollView>;
}
const styles = StyleSheet.create({ container: { alignSelf: "center", gap: 16, maxWidth: 720, padding: 16, width: "100%" }, form: { gap: 14 }, row: { flexDirection: "row", flexWrap: "wrap", gap: 12 }, field: { flex: 1, minWidth: 180 }, number: { flexBasis: 140, flexGrow: 1 }, state: { flexBasis: 100, flexGrow: 0 } });
