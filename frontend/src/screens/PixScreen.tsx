import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { payByKey, payDynamicQr, payStaticQr } from "@/core/pix/pix-api";
import { decodePixPayload } from "@/core/pix/pix-emv";
import { useActiveAccount } from "@/core/account/account-store";

export function PixScreen() {
  const [key, setKey] = useState("");
  const [amount, setAmount] = useState("");
  const [qrCode, setQrCode] = useState("");
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

  return (
    <View style={styles.container}>
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
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
