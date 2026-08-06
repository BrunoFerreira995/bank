import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { lookupBill, payBill, validateBillCode } from "@/core/payments/payments-api";

export function PaymentsScreen() {
  const [code, setCode] = useState("");
  const [billId, setBillId] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function lookup() {
    if (!validateBillCode(code))
      return setMessage("Informe uma linha digitável ou código de barras válido.");
    setLoading(true);
    try {
      const bill = await lookupBill(code);
      setBillId(bill.id);
      setMessage(
        `Beneficiário: ${bill.beneficiary ?? "não informado"} · R$ ${bill.amount.toFixed(
          2,
        )} · vencimento ${bill.dueDate}`,
      );
    } catch {
      setMessage("Não foi possível consultar o boleto.");
    } finally {
      setLoading(false);
    }
  }

  async function pay() {
    if (!billId) return;
    setLoading(true);
    try {
      const result = await payBill(billId, `bill-${billId}`);
      setMessage(`Boleto ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Pagamento indisponível ou em análise.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Boletos e pagamentos
      </Text>
      <TextInput
        accessibilityLabel="Linha digitável"
        placeholder="Linha digitável ou código de barras"
        value={code}
        onChangeText={setCode}
        keyboardType="number-pad"
      />
      <Button title="Consultar boleto" disabled={loading} onPress={() => lookup()} />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
      {billId ? (
        <Button title="Autorizar e pagar" disabled={loading} onPress={() => pay()} />
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
