import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";
import { Button, Card, Text, TextInput } from "react-native-paper";
import { closeAccount, createTed, getJudicialBlocks, openIncomeReport } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";
import { sharedStyles } from "@/ui/layout";

export function AccountServicesScreen() {
  const accountId = useActiveAccount((state) => state.accountId); const blocks = useQuery({ queryKey: ["judicial-blocks", accountId], queryFn: () => getJudicialBlocks(accountId!), enabled: !!accountId });
  const [reason, setReason] = useState(""); const [amount, setAmount] = useState(""); const [document, setDocument] = useState(""); const [message, setMessage] = useState<string | null>(null); const [busy, setBusy] = useState(false);
  async function requestClosure() { if (!accountId || !reason.trim()) return setMessage("Informe o motivo para solicitar o encerramento."); setBusy(true); try { await closeAccount(accountId, reason.trim()); setMessage("Solicitação de encerramento registrada."); } catch { setMessage("Não foi possível solicitar o encerramento."); } finally { setBusy(false); } }
  async function submitTransfer() { const value = Number(amount.replace(",", ".")); if (!accountId || !Number.isFinite(value) || value <= 0 || !document.trim()) return setMessage("Confira o valor e o documento do favorecido."); setBusy(true); try { await createTed({ accountId, amount: value, destination: { bankCode: "000", branch: "0001", number: "00000-0", document } }); setMessage("TED enviada para processamento."); setAmount(""); setDocument(""); } catch { setMessage("Não foi possível enviar a TED."); } finally { setBusy(false); } }
  return <ScrollView contentContainerStyle={sharedStyles.page}>
    <Text accessibilityRole="header" style={sharedStyles.title}>Serviços da conta</Text>
    <View style={styles.grid}>
      <Card mode="outlined" style={styles.card}><Card.Title title="Bloqueio judicial" /><Card.Content style={styles.section}>{blocks.isLoading ? <Text>Consultando bloqueios...</Text> : null}{blocks.isError ? <Text accessibilityRole="alert" style={sharedStyles.error}>Não foi possível consultar os bloqueios.</Text> : null}{!blocks.isLoading && !blocks.isError && !blocks.data?.length ? <Text style={sharedStyles.subtitle}>Nenhum bloqueio judicial encontrado.</Text> : null}{blocks.data?.map((block) => <Text key={block.id}>R$ {block.amount.toFixed(2)} · {block.status}</Text>)}</Card.Content></Card>
      <Card mode="outlined" style={styles.card}><Card.Title title="Informe de rendimentos" subtitle="Documento disponível somente em ambiente seguro." /><Card.Content><Button mode="outlined" icon="↓" onPress={() => openIncomeReport(2025).catch(() => setMessage("Não foi possível abrir o informe."))}>Abrir informe de 2025</Button></Card.Content></Card>
    </View>
    <Card mode="outlined"><Card.Title title="Transferência TED" /><Card.Content style={styles.section}><TextInput accessibilityLabel="Valor da TED" label="Valor da TED" mode="outlined" keyboardType="decimal-pad" value={amount} onChangeText={setAmount} /><TextInput accessibilityLabel="Documento do favorecido" label="CPF ou CNPJ do favorecido" mode="outlined" value={document} onChangeText={setDocument} /><Button mode="contained" loading={busy} disabled={busy} onPress={submitTransfer}>Enviar TED</Button></Card.Content></Card>
    <Card mode="outlined" style={styles.danger}><Card.Title title="Encerramento da conta" subtitle="Esta solicitação é sensível e será analisada." /><Card.Content style={styles.section}><TextInput accessibilityLabel="Motivo do encerramento" label="Motivo do encerramento" mode="outlined" multiline value={reason} onChangeText={setReason} /><Button mode="outlined" textColor="#BA1A1A" buttonColor="#FFFFFF" disabled={busy || !reason.trim()} onPress={requestClosure}>Solicitar encerramento</Button></Card.Content></Card>
    {message ? <Text accessibilityRole="alert" style={message.includes("registrada") || message.includes("enviada") ? sharedStyles.message : sharedStyles.error}>{message}</Text> : null}
  </ScrollView>;
}
const styles = StyleSheet.create({ grid: { flexDirection: "row", flexWrap: "wrap", gap: 16 }, card: { flex: 1, minWidth: 260 }, section: { gap: 14 }, danger: { borderColor: "#BA1A1A", borderWidth: 1 } });
