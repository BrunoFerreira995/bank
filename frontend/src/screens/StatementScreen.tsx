import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { ScrollView, StyleSheet, View } from "react-native";
import { Button, Card, Chip, Text, TextInput } from "react-native-paper";
import { getStatement, type Transaction } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";
import { sharedStyles } from "@/ui/layout";
import { formatCurrency } from "@/utils/format";

const statuses: Array<Transaction["status"]> = ["PENDING", "COMPLETED", "FAILED", "REVERSED"];

export function StatementScreen() {
  const accountId = useActiveAccount((state) => state.accountId);
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState("");
  const selectedStatus = statuses.includes(status as Transaction["status"]) ? (status as Transaction["status"]) : undefined;
  const statement = useQuery({ queryKey: ["statement", accountId, page, status], queryFn: () => getStatement(accountId!, page, selectedStatus ? { status: selectedStatus } : {}), enabled: !!accountId });
  const items = statement.data?.items ?? [];
  return <ScrollView contentContainerStyle={sharedStyles.page} testID="statement-screen">
    <Text accessibilityRole="header" style={sharedStyles.title}>Extrato</Text>
    <Text style={sharedStyles.subtitle}>Acompanhe as movimentações da conta e filtre por status.</Text>
    <Card mode="outlined" style={sharedStyles.card}><Card.Content style={styles.filters}>
      <Text variant="titleSmall">Filtrar lançamentos</Text>
      <View style={styles.chips}>{["", ...statuses].map((value) => <Chip key={value || "all"} selected={status === value} onPress={() => { setPage(0); setStatus(value); }}>{value || "Todos"}</Chip>)}</View>
      <TextInput accessibilityLabel="Filtro de status do extrato" label="Status do lançamento" mode="outlined" placeholder="PENDING, COMPLETED..." value={status} onChangeText={(value) => { setPage(0); setStatus(value.toUpperCase()); }} />
      <Button mode="outlined" icon="↻" loading={statement.isFetching} onPress={() => statement.refetch()}>Atualizar extrato</Button>
    </Card.Content></Card>
    <Card mode="outlined" style={sharedStyles.card}><Card.Content style={styles.list}>
      {statement.isError ? <Text accessibilityRole="alert" style={sharedStyles.error}>Não foi possível carregar o extrato. Tente atualizar novamente.</Text> : null}
      {!statement.isFetching && !statement.isError && !items.length ? <View style={styles.empty}><Text variant="titleMedium">Nenhuma movimentação encontrada</Text><Text style={sharedStyles.subtitle}>Quando houver lançamentos, eles aparecerão aqui.</Text></View> : null}
      {items.map((item) => <View key={item.id} style={styles.item}><View style={styles.itemDescription}><Text variant="titleSmall">{item.description}</Text><Text style={styles.itemMeta}>{item.status} · {item.type}</Text></View><Text variant="titleSmall" style={styles.amount}>{formatCurrency(item.amount)}</Text></View>)}
    </Card.Content></Card>
    <View style={styles.pagination}><Button mode="outlined" disabled={page === 0} onPress={() => setPage((value) => value - 1)}>Anterior</Button><Text>Página {page + 1} de {statement.data?.totalPages ?? 1}</Text><Button mode="contained" disabled={!statement.data || page + 1 >= statement.data.totalPages} onPress={() => setPage((value) => value + 1)}>Próxima</Button></View>
  </ScrollView>;
}

const styles = StyleSheet.create({
  filters: { gap: 12 }, chips: { flexDirection: "row", flexWrap: "wrap", gap: 8 }, list: { gap: 4 },
  item: { alignItems: "center", borderBottomColor: "#E1E8E5", borderBottomWidth: 1, flexDirection: "row", gap: 12, justifyContent: "space-between", paddingVertical: 16 },
  itemDescription: { flex: 1, minWidth: 0 }, itemMeta: { color: "#52605C", fontSize: 14, marginTop: 3 }, amount: { color: "#006C5B", textAlign: "right" },
  empty: { alignItems: "center", gap: 8, paddingVertical: 32 }, pagination: { alignItems: "center", flexDirection: "row", flexWrap: "wrap", gap: 12, justifyContent: "space-between", paddingVertical: 4 },
});
