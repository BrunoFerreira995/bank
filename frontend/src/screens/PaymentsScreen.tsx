import { useState } from "react";
import { Button, ScrollView, StyleSheet, Text, TextInput } from "react-native";
import {
  authorizeBill,
  createTopUp,
  listTopUpOperators,
  lookupBill,
  lookupVehicleDebts,
  payBill,
  payVehicleDebts,
  validateAmount,
  validateBillCode,
  type VehicleDebt,
} from "@/core/payments/payments-api";

export function PaymentsScreen() {
  const [code, setCode] = useState("");
  const [billId, setBillId] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [operatorId, setOperatorId] = useState("");
  const [productId, setProductId] = useState("");
  const [phone, setPhone] = useState("");
  const [topUpAmount, setTopUpAmount] = useState("");
  const [vehicleDocument, setVehicleDocument] = useState("");
  const [renavam, setRenavam] = useState("");
  const [debts, setDebts] = useState<VehicleDebt[]>([]);

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
      await authorizeBill(billId);
      const result = await payBill(billId, `bill-${billId}`);
      setMessage(`Boleto ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Pagamento indisponível ou em análise.");
    } finally {
      setLoading(false);
    }
  }

  async function topUp() {
    const value = Number(topUpAmount.replace(",", "."));
    if (!operatorId || !productId || !phone || !validateAmount(value))
      return setMessage("Confira operadora, produto, telefone e valor.");
    setLoading(true);
    try {
      const result = await createTopUp(
        { operatorId, productId, phone, amount: value },
        `topup-${Date.now()}`,
      );
      setMessage(`Recarga ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Não foi possível realizar a recarga.");
    } finally {
      setLoading(false);
    }
  }

  async function lookupDebts() {
    if (!vehicleDocument || !renavam) return setMessage("Informe documento e RENAVAM.");
    setLoading(true);
    try {
      setDebts(await lookupVehicleDebts(vehicleDocument, renavam));
      setMessage("Débitos consultados.");
    } catch {
      setMessage("Não foi possível consultar os débitos veiculares.");
    } finally {
      setLoading(false);
    }
  }

  async function payDebts() {
    const openDebts = debts.filter((debt) => debt.status === "OPEN");
    if (!openDebts.length) return setMessage("Não há débitos abertos para pagamento.");
    setLoading(true);
    try {
      const result = await payVehicleDebts(
        openDebts.map((debt) => debt.id),
        `vehicle-${Date.now()}`,
      );
      setMessage(`Débitos ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Não foi possível pagar os débitos veiculares.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
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
      <Text style={styles.subtitle}>Recarga de celular</Text>
      <TextInput
        accessibilityLabel="Operadora"
        placeholder="ID da operadora"
        value={operatorId}
        onChangeText={setOperatorId}
      />
      <TextInput
        accessibilityLabel="Produto da recarga"
        placeholder="ID do produto"
        value={productId}
        onChangeText={setProductId}
      />
      <TextInput
        accessibilityLabel="Telefone da recarga"
        placeholder="Telefone"
        value={phone}
        onChangeText={setPhone}
      />
      <TextInput
        accessibilityLabel="Valor da recarga"
        placeholder="Valor"
        keyboardType="decimal-pad"
        value={topUpAmount}
        onChangeText={setTopUpAmount}
      />
      <Button title="Realizar recarga" disabled={loading} onPress={() => topUp()} />
      <Button
        title="Listar operadoras"
        onPress={() =>
          listTopUpOperators()
            .then(() => setMessage("Operadoras carregadas."))
            .catch(() => setMessage("Não foi possível carregar as operadoras."))
        }
      />
      <Text style={styles.subtitle}>Débitos veiculares</Text>
      <TextInput
        accessibilityLabel="Documento veicular"
        placeholder="CPF ou CNPJ"
        value={vehicleDocument}
        onChangeText={setVehicleDocument}
      />
      <TextInput
        accessibilityLabel="RENAVAM"
        placeholder="RENAVAM"
        value={renavam}
        onChangeText={setRenavam}
      />
      <Button title="Consultar débitos" disabled={loading} onPress={() => lookupDebts()} />
      {debts.map((debt) => (
        <Text key={debt.id}>
          {debt.description} · R$ {debt.amount.toFixed(2)} · {debt.status}
        </Text>
      ))}
      {debts.length ? (
        <Button title="Pagar débitos selecionados" disabled={loading} onPress={() => payDebts()} />
      ) : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 12 },
});
