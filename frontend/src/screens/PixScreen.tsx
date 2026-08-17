import { useState } from "react";
import { Button, ScrollView, StyleSheet, Text, TextInput } from "react-native";
import {
  createImmediateCharge,
  payByBankDetails,
  payByKey,
  payDynamicQr,
  payStaticQr,
} from "@/core/pix/pix-api";
import { decodePixPayload } from "@/core/pix/pix-emv";
import { useActiveAccount } from "@/core/account/account-store";

export function PixScreen({
  navigation,
}: {
  navigation?: { navigate: (route: "PixManagement") => void };
}) {
  const [key, setKey] = useState("");
  const [amount, setAmount] = useState("");
  const [qrCode, setQrCode] = useState("");
  const [bankCode, setBankCode] = useState("");
  const [branch, setBranch] = useState("");
  const [account, setAccount] = useState("");
  const [document, setDocument] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const accountId = useActiveAccount((state) => state.accountId);

  async function pay(mode: "key" | "qr") {
    setLoading(true);
    setMessage(null);
    try {
      const value = Number(amount.replace(",", "."));
      if (!Number.isFinite(value) || value <= 0) throw new Error("Informe um valor válido");
      if (!accountId) throw new Error("Selecione uma conta antes de pagar");
      if (mode === "key" && !key.trim()) throw new Error("Informe a chave Pix");
      if (mode === "qr") decodePixPayload(qrCode);
      const response =
        mode === "key"
          ? await payByKey({ accountId, amount: value, key: key.trim() })
          : qrCode.includes("6304")
          ? await payDynamicQr({ accountId, amount: value, qrCode })
          : await payStaticQr({ accountId, amount: value, qrCode });
      setMessage(`Pix ${response.status.toLowerCase()}: ${response.id}`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Não foi possível processar o Pix.");
    } finally {
      setLoading(false);
    }
  }

  async function payBankDetails() {
    setLoading(true);
    setMessage(null);
    try {
      const value = Number(amount.replace(",", "."));
      if (!accountId || !Number.isFinite(value) || value <= 0)
        throw new Error("Informe um valor válido");
      if (!bankCode || !branch || !account || !document)
        throw new Error("Informe os dados bancários");
      const response = await payByBankDetails({
        accountId,
        amount: value,
        beneficiary: { name: "Favorecido", bankCode, branch, account, document },
      });
      setMessage(`Pix ${response.status.toLowerCase()}: ${response.id}`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Não foi possível processar o Pix.");
    } finally {
      setLoading(false);
    }
  }

  async function createCharge() {
    setLoading(true);
    setMessage(null);
    try {
      const value = Number(amount.replace(",", "."));
      if (!Number.isFinite(value) || value <= 0) throw new Error("Informe um valor válido");
      const charge = await createImmediateCharge({ type: "IMMEDIATE", amount: value });
      setMessage(`Cobrança criada: ${charge.id}`);
    } catch {
      setMessage("Não foi possível criar a cobrança Pix.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Pix
      </Text>
      <TextInput
        accessibilityLabel="Chave Pix"
        placeholder="Chave Pix"
        value={key}
        onChangeText={setKey}
      />
      <TextInput
        accessibilityLabel="Valor"
        keyboardType="decimal-pad"
        placeholder="Valor"
        value={amount}
        onChangeText={setAmount}
      />
      <Button disabled={loading} title="Pagar por chave" onPress={() => pay("key")} />
      <Text style={styles.subtitle}>Dados bancários</Text>
      <TextInput
        accessibilityLabel="Código do banco"
        placeholder="Código do banco"
        value={bankCode}
        onChangeText={setBankCode}
      />
      <TextInput
        accessibilityLabel="Agência"
        placeholder="Agência"
        value={branch}
        onChangeText={setBranch}
      />
      <TextInput
        accessibilityLabel="Conta"
        placeholder="Conta"
        value={account}
        onChangeText={setAccount}
      />
      <TextInput
        accessibilityLabel="Documento do favorecido Pix"
        placeholder="CPF ou CNPJ"
        value={document}
        onChangeText={setDocument}
      />
      <Button
        disabled={loading}
        title="Pagar por dados bancários"
        onPress={() => payBankDetails()}
      />
      <TextInput
        accessibilityLabel="Código Pix"
        multiline
        placeholder="Cole o código Pix"
        value={qrCode}
        onChangeText={setQrCode}
      />
      <Button
        disabled={loading || !qrCode}
        title="Validar e pagar QR Code"
        onPress={() => pay("qr")}
      />
      <Text style={styles.subtitle}>Cobrança Pix</Text>
      <Button disabled={loading} title="Criar cobrança imediata" onPress={() => createCharge()} />
      <Button
        title="Gerenciar chaves e operações Pix"
        onPress={() => navigation?.navigate("PixManagement")}
      />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 12 },
});
