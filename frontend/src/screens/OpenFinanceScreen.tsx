import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import { env } from "@/config/env";
import {
  createConsent,
  listConsents,
  listInstitutions,
  revokeConsent,
  initiateImmediatePayment,
  initiateScheduledPayment,
  initiateRedirectFlow,
  initiateAutomaticPayment,
  createSweepingTransfer,
  createBrickSession,
  listLinks,
} from "@/core/open-finance/open-finance-api";
import { openOpenFinanceRedirect } from "@/core/open-finance/redirect-flow";

export function OpenFinanceScreen() {
  const [message, setMessage] = useState<string | null>(null);
  const [amount, setAmount] = useState("");
  const [beneficiary, setBeneficiary] = useState("");
  const [scheduledFor, setScheduledFor] = useState("");
  const [frequency, setFrequency] = useState("MONTHLY");
  const [sourceAccount, setSourceAccount] = useState("");
  const [targetAccount, setTargetAccount] = useState("");
  const institutions = useQuery({
    queryKey: ["open-finance-institutions"],
    queryFn: listInstitutions,
    enabled: env.flags.openFinance,
  });
  const consents = useQuery({
    queryKey: ["open-finance-consents"],
    queryFn: listConsents,
    enabled: env.flags.openFinance,
  });
  const links = useQuery({
    queryKey: ["open-finance-links"],
    queryFn: listLinks,
    enabled: env.flags.openFinance,
  });
  async function authorize(institutionId: string) {
    try {
      await createConsent(institutionId, ["ACCOUNTS", "TRANSACTIONS"]);
      setMessage("Consentimento criado. Continue na instituição selecionada.");
      consents.refetch();
    } catch {
      setMessage("Instituição indisponível ou consentimento recusado.");
    }
  }
  async function revoke(id: string) {
    try {
      await revokeConsent(id);
      await consents.refetch();
      setMessage("Consentimento revogado.");
    } catch {
      setMessage("Não foi possível revogar o consentimento.");
    }
  }
  async function redirect(institutionId: string, consentId: string) {
    try {
      const flow = await initiateRedirectFlow(institutionId, consentId);
      if (!flow.redirectUrl) throw new Error("Redirecionamento indisponível");
      await openOpenFinanceRedirect(flow.redirectUrl);
    } catch {
      setMessage("Não foi possível iniciar o redirecionamento.");
    }
  }
  async function pay(scheduled: boolean, automatic = false) {
    const consent = consents.data?.find((item) => item.status === "AUTHORIZED");
    const value = Number(amount.replace(",", "."));
    if (!consent || !Number.isFinite(value) || value <= 0 || !beneficiary.trim())
      return setMessage("Autorize um consentimento e confira valor e favorecido.");
    try {
      const result = automatic
        ? await initiateAutomaticPayment(
            { consentId: consent.id, amount: value, beneficiary, frequency },
            `open-finance-${Date.now()}`,
          )
        : scheduled
        ? await initiateScheduledPayment(
            { consentId: consent.id, amount: value, beneficiary, scheduledFor },
            `open-finance-${Date.now()}`,
          )
        : await initiateImmediatePayment(
            { consentId: consent.id, amount: value, beneficiary },
            `open-finance-${Date.now()}`,
          );
      setMessage(`Pagamento ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Não foi possível iniciar o pagamento Open Finance.");
    }
  }
  async function sweep() {
    const value = Number(amount.replace(",", "."));
    if (!sourceAccount || !targetAccount || !Number.isFinite(value) || value <= 0)
      return setMessage("Confira contas de origem, destino e valor.");
    try {
      const result = await createSweepingTransfer(
        { sourceAccount, targetAccount, amount: value },
        `sweeping-${Date.now()}`,
      );
      setMessage(`Sweeping ${result.status.toLowerCase()}: ${result.id}`);
    } catch {
      setMessage("Não foi possível criar o sweeping.");
    }
  }
  async function createBrick(product: "BANK" | "INSURANCE") {
    try {
      const flow = await createBrickSession(product);
      setMessage(`Brick ${product}: ${flow.status.toLowerCase()}.`);
    } catch {
      setMessage(`Não foi possível iniciar o Brick ${product}.`);
    }
  }
  if (!env.flags.openFinance) return <Text>Open Finance indisponível para este contrato.</Text>;
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Open Finance
      </Text>
      <Text>Instituições disponíveis</Text>
      {institutions.data?.map((institution) => (
        <Button
          key={institution.id}
          disabled={institution.status !== "AVAILABLE"}
          title={institution.name}
          onPress={() => authorize(institution.id)}
        />
      ))}
      <Text>Consentimentos e vínculos</Text>
      {consents.data?.map((consent) => (
        <View key={consent.id}>
          <Text>
            {consent.institutionName} · {consent.status}
          </Text>
          <Button title="Revogar" onPress={() => revoke(consent.id)} />
          {consent.status === "AUTHORIZED" ? (
            <Button
              title="Refazer vínculo"
              onPress={() => redirect(consent.institutionId, consent.id)}
            />
          ) : null}
        </View>
      ))}
      <Text style={styles.subtitle}>Pagamento Open Finance</Text>
      <TextInput
        accessibilityLabel="Valor Open Finance"
        placeholder="Valor"
        keyboardType="decimal-pad"
        value={amount}
        onChangeText={setAmount}
      />
      <TextInput
        accessibilityLabel="Favorecido Open Finance"
        placeholder="Favorecido"
        value={beneficiary}
        onChangeText={setBeneficiary}
      />
      <TextInput
        accessibilityLabel="Data do agendamento"
        placeholder="Data (AAAA-MM-DD)"
        value={scheduledFor}
        onChangeText={setScheduledFor}
      />
      <Button title="Pagar agora" onPress={() => pay(false)} />
      <Button title="Agendar pagamento" onPress={() => pay(true)} />
      <TextInput
        accessibilityLabel="Frequência do pagamento"
        placeholder="Frequência"
        value={frequency}
        onChangeText={setFrequency}
      />
      <Button title="Autorizar pagamento automático" onPress={() => pay(false, true)} />
      <Text style={styles.subtitle}>Sweeping Accounts</Text>
      <TextInput
        accessibilityLabel="Conta de origem"
        placeholder="Conta de origem"
        value={sourceAccount}
        onChangeText={setSourceAccount}
      />
      <TextInput
        accessibilityLabel="Conta de destino"
        placeholder="Conta de destino"
        value={targetAccount}
        onChangeText={setTargetAccount}
      />
      <Button title="Criar sweeping" onPress={() => sweep()} />
      <Text style={styles.subtitle}>Jornadas e Bricks</Text>
      {links.data?.map((link) => (
        <Text key={link.id}>
          Jornada {link.id} · {link.status}
        </Text>
      ))}
      <Button title="Iniciar Brick Bank" onPress={() => createBrick("BANK")} />
      <Button title="Iniciar Brick Insurance" onPress={() => createBrick("INSURANCE")} />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 16 },
});
