import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Button, FlatList, StyleSheet, Text, TextInput, View } from "react-native";
import { getStatement } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";

export function StatementScreen() {
  const accountId = useActiveAccount((state) => state.accountId);
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState("");
  const statement = useQuery({
    queryKey: ["statement", accountId, page, status],
    queryFn: () => getStatement(accountId!, page, status ? { status: status as "PENDING" } : {}),
    enabled: !!accountId,
  });
  return (
    <View style={styles.container}>
      <TextInput
        placeholder="Status (PENDING, COMPLETED...)"
        value={status}
        onChangeText={(value) => {
          setPage(0);
          setStatus(value.toUpperCase());
        }}
      />
      <FlatList
        data={statement.data?.items ?? []}
        keyExtractor={(item) => item.id}
        onRefresh={() => statement.refetch()}
        refreshing={statement.isFetching}
        renderItem={({ item }) => (
          <Text style={styles.item}>
            {item.description} · {item.status} · R$ {item.amount.toFixed(2)}
          </Text>
        )}
        ListEmptyComponent={<Text>Nenhuma movimentação encontrada.</Text>}
      />
      <View style={styles.pagination}>
        <Button
          title="Anterior"
          disabled={page === 0}
          onPress={() => setPage((value) => value - 1)}
        />
        <Text>Página {page + 1}</Text>
        <Button
          title="Próxima"
          disabled={!statement.data || page + 1 >= statement.data.totalPages}
          onPress={() => setPage((value) => value + 1)}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 12, padding: 24 },
  item: { paddingVertical: 12 },
  pagination: { alignItems: "center", flexDirection: "row", justifyContent: "space-between" },
});
