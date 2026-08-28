import { useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";
import { Button, Card, Text, TextInput } from "react-native-paper";
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
    <ScrollView testID="pix-screen" contentContainerStyle={styles.container}>
      <Text accessibilityRole="header" variant="headlineMedium">
        Pix
      </Text>
      <Text variant="bodyMedium" style={styles.description}>
        Pague com chave, dados bancários ou QR Code.
      </Text>
      <Card mode="elevated">
        <Card.Title title="Pix por chave" />
        <Card.Content style={styles.section}>
          <TextInput
            accessibilityLabel="Chave Pix"
            label="Chave Pix"
            mode="outlined"
            placeholder="Chave Pix"
            value={key}
            onChangeText={setKey}
          />
          <TextInput
            accessibilityLabel="Valor"
            keyboardType="decimal-pad"
            label="Valor"
            mode="outlined"
            placeholder="Valor"
            value={amount}
            onChangeText={setAmount}
          />
          <Button loading={loading} disabled={loading} mode="contained" onPress={() => pay("key")}>
            Pagar por chave
          </Button>
        </Card.Content>
      </Card>
      <Card mode="outlined">
        <Card.Title title="Dados bancários" />
        <Card.Content style={styles.section}>
          <View style={styles.row}>
            <TextInput
              accessibilityLabel="Código do banco"
              label="Banco"
              mode="outlined"
              placeholder="Código do banco"
              style={styles.flexInput}
              value={bankCode}
              onChangeText={setBankCode}
            />
            <TextInput
              accessibilityLabel="Agência"
              label="Agência"
              mode="outlined"
              placeholder="Agência"
              style={styles.flexInput}
              value={branch}
              onChangeText={setBranch}
            />
          </View>
          <TextInput
            accessibilityLabel="Conta"
            label="Conta"
            mode="outlined"
            placeholder="Conta"
            value={account}
            onChangeText={setAccount}
          />
          <TextInput
            accessibilityLabel="Documento do favorecido Pix"
            label="CPF ou CNPJ do favorecido"
            mode="outlined"
            placeholder="CPF ou CNPJ"
            value={document}
            onChangeText={setDocument}
          />
          <Button disabled={loading} mode="contained-tonal" onPress={() => payBankDetails()}>
            Pagar por dados bancários
          </Button>
        </Card.Content>
      </Card>
      <Card mode="outlined">
        <Card.Title title="QR Code e cobrança" />
        <Card.Content style={styles.section}>
          <TextInput
            accessibilityLabel="Código Pix"
            label="Código Pix"
            mode="outlined"
            multiline
            placeholder="Cole o código Pix"
            value={qrCode}
            onChangeText={setQrCode}
          />
          <Button disabled={loading || !qrCode} mode="contained-tonal" onPress={() => pay("qr")}>
            Validar e pagar QR Code
          </Button>
          <Button disabled={loading} mode="outlined" onPress={() => createCharge()}>
            Criar cobrança imediata
          </Button>
          <Button mode="text" onPress={() => navigation?.navigate("PixManagement")}>
            Gerenciar chaves e operações Pix
          </Button>
        </Card.Content>
      </Card>
      {message ? (
        <Text accessibilityRole="alert" style={styles.message}>
          {message}
        </Text>
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { gap: 16, padding: 24 },
  description: { color: "#3F4946", marginBottom: 4 },
  section: { gap: 12 },
  row: { flexDirection: "row", gap: 12 },
  flexInput: { flex: 1 },
  message: { color: "#006C5B", paddingVertical: 8 },
});
