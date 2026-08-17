import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import {
  createDueDateCharge,
  createPixKey,
  createStaticCharge,
  deletePixKey,
  getPixPayments,
  listPixKeys,
  refundPix,
  type PixKey,
} from "@/core/pix/pix-api";
import { useActiveAccount } from "@/core/account/account-store";

export function PixManagementScreen() {
  const accountId = useActiveAccount((state) => state.accountId);
  const keys = useQuery({
    queryKey: ["pix-keys", accountId],
    queryFn: () => listPixKeys(accountId!),
    enabled: !!accountId,
  });
  const payments = useQuery({ queryKey: ["pix-payments"], queryFn: getPixPayments });
  const [key, setKey] = useState("");
  const [keyType, setKeyType] = useState<PixKey["type"]>("RANDOM");
  const [amount, setAmount] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [paymentId, setPaymentId] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  async function addKey() {
    if (!accountId || !key.trim()) return setMessage("Informe a chave Pix.");
    try {
      await createPixKey(accountId, { key: key.trim(), type: keyType });
      setKey("");
      setMessage("Chave Pix criada.");
      await keys.refetch();
    } catch {
      setMessage("Não foi possível criar a chave Pix.");
    }
  }

  async function removeKey(value: string) {
    try {
      await deletePixKey(value);
      setMessage("Chave Pix excluída.");
      await keys.refetch();
    } catch {
      setMessage("Não foi possível excluir a chave Pix.");
    }
  }

  async function createCharge(type: "DUEDATE" | "STATIC") {
    const value = Number(amount.replace(",", "."));
    if (!Number.isFinite(value) || value <= 0) return setMessage("Informe um valor válido.");
    try {
      const charge =
        type === "DUEDATE"
          ? await createDueDateCharge({ type, amount: value, dueDate })
          : await createStaticCharge({ type, amount: value });
      setMessage(`Cobrança criada: ${charge.id}`);
    } catch {
      setMessage("Não foi possível criar a cobrança Pix.");
    }
  }

  async function refund() {
    if (!paymentId.trim()) return setMessage("Informe o pagamento Pix.");
    try {
      const result = await refundPix(paymentId.trim());
      setMessage(`Devolução ${result.status.toLowerCase()}.`);
    } catch {
      setMessage("Não foi possível solicitar a devolução.");
    }
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Gestão Pix
      </Text>
      <Text style={styles.subtitle}>Minhas chaves</Text>
      {keys.data?.map((item) => (
        <View key={item.key} style={styles.row}>
          <Text>
            {item.key} · {item.type} · {item.status}
          </Text>
          <Button title="Excluir" onPress={() => removeKey(item.key)} />
        </View>
      ))}
      {!keys.isLoading && !keys.data?.length ? <Text>Nenhuma chave Pix cadastrada.</Text> : null}
      <TextInput
        accessibilityLabel="Nova chave Pix"
        placeholder="Nova chave Pix"
        value={key}
        onChangeText={setKey}
      />
      <TextInput
        accessibilityLabel="Tipo da chave Pix"
        placeholder="Tipo (RANDOM, EMAIL...)"
        value={keyType}
        onChangeText={(value) => setKeyType(value.toUpperCase() as PixKey["type"])}
      />
      <Button title="Criar chave Pix" onPress={() => addKey()} />
      <Text style={styles.subtitle}>Cobranças</Text>
      <TextInput
        accessibilityLabel="Valor da cobrança"
        placeholder="Valor"
        keyboardType="decimal-pad"
        value={amount}
        onChangeText={setAmount}
      />
      <TextInput
        accessibilityLabel="Vencimento da cobrança"
        placeholder="Vencimento (AAAA-MM-DD)"
        value={dueDate}
        onChangeText={setDueDate}
      />
      <Button title="Criar cobrança com vencimento" onPress={() => createCharge("DUEDATE")} />
      <Button title="Criar cobrança estática" onPress={() => createCharge("STATIC")} />
      <Text style={styles.subtitle}>Devolução</Text>
      <TextInput
        accessibilityLabel="ID do pagamento Pix"
        placeholder="ID do pagamento"
        value={paymentId}
        onChangeText={setPaymentId}
      />
      <Button title="Solicitar devolução" onPress={() => refund()} />
      {payments.isError ? (
        <Text accessibilityRole="alert">Não foi possível carregar os pagamentos Pix.</Text>
      ) : null}
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { gap: 12, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 16 },
  row: { alignItems: "center", flexDirection: "row", justifyContent: "space-between" },
});
