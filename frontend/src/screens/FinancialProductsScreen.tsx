import { useQuery } from "@tanstack/react-query";
import { Button, StyleSheet, Text, View } from "react-native";
import { listCards, requestCard } from "@/core/financial/cards-api";
import { listEscrows } from "@/core/financial/escrow-api";

export function FinancialProductsScreen() {
  const cards = useQuery({ queryKey: ["cards"], queryFn: listCards });
  const escrows = useQuery({ queryKey: ["escrow"], queryFn: listEscrows });
  async function requestVirtualCard() {
    await requestCard("VIRTUAL");
    cards.refetch();
  }
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Cartões, crédito e Escrow
      </Text>
      <Text>Cartões</Text>
      {cards.data?.map((card) => (
        <Text key={card.id}>
          {card.brand} •••• {card.lastFour} · {card.status} · limite R${" "}
          {card.availableLimit.toFixed(2)}
        </Text>
      ))}
      <Button title="Solicitar cartão virtual" onPress={() => requestVirtualCard()} />
      <Text>Contas Escrow</Text>
      {escrows.data?.map((escrow) => (
        <Text key={escrow.id}>
          {escrow.id} · {escrow.status} · R$ {escrow.balance.toFixed(2)}
        </Text>
      ))}
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
});
