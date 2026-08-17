import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import {
  closeAccount,
  createTed,
  getJudicialBlocks,
  openIncomeReport,
} from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";

export function AccountServicesScreen() {
  const accountId = useActiveAccount((state) => state.accountId);
  const blocks = useQuery({
    queryKey: ["judicial-blocks", accountId],
    queryFn: () => getJudicialBlocks(accountId!),
    enabled: !!accountId,
  });
  const [reason, setReason] = useState("");
  const [amount, setAmount] = useState("");
  const [document, setDocument] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function requestClosure() {
    if (!accountId || !reason.trim())
      return setMessage("Informe o motivo para solicitar o encerramento.");
    setBusy(true);
    setMessage(null);
    try {
      await closeAccount(accountId, reason.trim());
      setMessage("Solicitação de encerramento registrada.");
    } catch {
      setMessage("Não foi possível solicitar o encerramento.");
    } finally {
      setBusy(false);
    }
  }

  async function submitTransfer() {
    const value = Number(amount.replace(",", "."));
    if (!accountId || !Number.isFinite(value) || value <= 0 || !document.trim())
      return setMessage("Confira o valor e o documento do favorecido.");
    setBusy(true);
    setMessage(null);
    try {
      await createTed({
        accountId,
        amount: value,
        destination: { bankCode: "000", branch: "0001", number: "00000-0", document },
      });
      setMessage("TED enviada para processamento.");
      setAmount("");
      setDocument("");
    } catch {
      setMessage("Não foi possível enviar a TED.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Serviços da conta
      </Text>
      <Text style={styles.subtitle}>Bloqueio judicial</Text>
      {blocks.isLoading ? <Text>Consultando bloqueios...</Text> : null}
      {blocks.isError ? (
        <Text accessibilityRole="alert">Não foi possível consultar os bloqueios.</Text>
      ) : null}
      {!blocks.isLoading && !blocks.isError && !blocks.data?.length ? (
        <Text>Nenhum bloqueio judicial encontrado.</Text>
      ) : null}
      {blocks.data?.map((block) => (
        <Text key={block.id}>
          R$ {block.amount.toFixed(2)} · {block.status}
        </Text>
      ))}
      <Text style={styles.subtitle}>Informe de rendimentos</Text>
      <Button
        title="Abrir informe de 2025"
        onPress={() =>
          openIncomeReport(2025).catch(() => setMessage("Não foi possível abrir o informe."))
        }
      />
      <Text style={styles.subtitle}>Transferência TED</Text>
      <TextInput
        accessibilityLabel="Valor da TED"
        placeholder="Valor"
        keyboardType="decimal-pad"
        value={amount}
        onChangeText={setAmount}
      />
      <TextInput
        accessibilityLabel="Documento do favorecido"
        placeholder="CPF ou CNPJ do favorecido"
        value={document}
        onChangeText={setDocument}
      />
      <Button title="Enviar TED" disabled={busy} onPress={() => submitTransfer()} />
      <Text style={styles.subtitle}>Encerramento da conta</Text>
      <TextInput
        accessibilityLabel="Motivo do encerramento"
        placeholder="Motivo"
        value={reason}
        onChangeText={setReason}
      />
      <Button title="Solicitar encerramento" disabled={busy} onPress={() => requestClosure()} />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { gap: 12, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 16 },
});
