import { useQuery } from "@tanstack/react-query";
import { Button, StyleSheet, Text, View } from "react-native";
import { useState } from "react";
import {
  activateCard,
  getCardStatement,
  listCards,
  requestCard,
  setCardBlocked,
} from "@/core/financial/cards-api";
import { getEscrowEvents, listEscrows } from "@/core/financial/escrow-api";
import { simulateCredit } from "@/core/financial/credit-api";
import { TextInput } from "react-native";

export function FinancialProductsScreen() {
  const cards = useQuery({ queryKey: ["cards"], queryFn: listCards });
  const escrows = useQuery({ queryKey: ["escrow"], queryFn: listEscrows });
  const card = cards.data?.[0];
  const statement = useQuery({
    queryKey: ["card-statement", card?.id],
    queryFn: () => getCardStatement(card!.id),
    enabled: !!card,
  });
  const events = useQuery({
    queryKey: ["escrow-events", escrows.data?.[0]?.id],
    queryFn: () => getEscrowEvents(escrows.data?.[0]?.id!),
    enabled: !!escrows.data?.[0],
  });
  const [activationCode, setActivationCode] = useState("");
  const [creditAmount, setCreditAmount] = useState("");
  const [installments, setInstallments] = useState("12");
  const [creditMessage, setCreditMessage] = useState<string | null>(null);
  async function requestVirtualCard() {
    try {
      await requestCard("VIRTUAL");
      await cards.refetch();
    } catch {
      setCreditMessage("Não foi possível solicitar o cartão.");
    }
  }
  async function activate() {
    if (!card || activationCode.length !== 4)
      return setCreditMessage("Informe os quatro últimos dígitos do cartão.");
    try {
      await activateCard(card.id, activationCode);
      setCreditMessage("Cartão ativado.");
      await cards.refetch();
    } catch {
      setCreditMessage("Não foi possível ativar o cartão.");
    }
  }
  async function toggleBlock() {
    if (!card) return;
    try {
      await setCardBlocked(card.id, card.status !== "BLOCKED");
      setCreditMessage("Status do cartão atualizado.");
      await cards.refetch();
    } catch {
      setCreditMessage("Não foi possível atualizar o bloqueio.");
    }
  }
  async function simulate() {
    const amount = Number(creditAmount.replace(",", "."));
    const count = Number(installments);
    if (!Number.isFinite(amount) || amount <= 0 || !Number.isInteger(count) || count <= 0)
      return setCreditMessage("Informe valor e parcelas válidos.");
    try {
      const result = await simulateCredit({ product: "PERSONAL", amount, installments: count });
      setCreditMessage(
        `Simulação: ${result.installments}x de R$ ${result.installmentAmount.toFixed(2)}.`,
      );
    } catch {
      setCreditMessage("Não foi possível simular o crédito.");
    }
  }
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Cartões, crédito e Escrow
      </Text>
      <Text>Cartões</Text>
      {cards.data?.map((item) => (
        <Text key={item.id}>
          {item.brand} •••• {item.lastFour} · {item.status} · limite R${" "}
          {item.availableLimit.toFixed(2)}
        </Text>
      ))}
      <Button title="Solicitar cartão virtual" onPress={() => requestVirtualCard()} />
      {card ? (
        <>
          <TextInput
            accessibilityLabel="Últimos quatro dígitos"
            placeholder="Últimos quatro dígitos"
            keyboardType="number-pad"
            maxLength={4}
            value={activationCode}
            onChangeText={setActivationCode}
          />
          <Button title="Ativar cartão" onPress={() => activate()} />
          <Button
            title={card.status === "BLOCKED" ? "Desbloquear cartão" : "Bloquear cartão"}
            onPress={() => toggleBlock()}
          />
          {statement.data ? (
            <Text>
              Fatura: R$ {statement.data.total.toFixed(2)} · vencimento {statement.data.dueDate}
            </Text>
          ) : null}
        </>
      ) : null}
      <Text style={styles.subtitle}>Simulação de crédito</Text>
      <TextInput
        accessibilityLabel="Valor do crédito"
        placeholder="Valor"
        keyboardType="decimal-pad"
        value={creditAmount}
        onChangeText={setCreditAmount}
      />
      <TextInput
        accessibilityLabel="Quantidade de parcelas"
        placeholder="Parcelas"
        keyboardType="number-pad"
        value={installments}
        onChangeText={setInstallments}
      />
      <Button title="Simular crédito" onPress={() => simulate()} />
      <Text>Contas Escrow</Text>
      {escrows.data?.map((escrow) => (
        <Text key={escrow.id}>
          {escrow.id} · {escrow.status} · R$ {escrow.balance.toFixed(2)}
        </Text>
      ))}
      {events.data?.map((event) => (
        <Text key={event.id}>
          Evento {event.type} · {event.status}
        </Text>
      ))}
      {creditMessage ? <Text accessibilityRole="alert">{creditMessage}</Text> : null}
      <Text>
        Crédito, consignado e portabilidade ficam disponíveis conforme elegibilidade e contrato do
        BFF.
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, padding: 24 },
  title: { fontSize: 26, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 12 },
});
