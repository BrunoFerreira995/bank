import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";
import {
  ActivityIndicator,
  Button,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
} from "react-native";
import { getBalance, getDailyMovements, listAccounts } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";

export function AccountDashboardScreen({
  navigation,
}: {
  navigation: { navigate: (route: "Statement" | "Profile") => void };
}) {
  const { accountId, setAccount } = useActiveAccount();
  const accounts = useQuery({ queryKey: ["accounts"], queryFn: listAccounts });
  const selectedId = accountId ?? accounts.data?.[0]?.id;
  const balance = useQuery({
    queryKey: ["balance", selectedId],
    queryFn: () => getBalance(selectedId!),
    enabled: !!selectedId,
  });
  const daily = useQuery({
    queryKey: ["movements-today", selectedId],
    queryFn: () => getDailyMovements(selectedId!),
    enabled: !!selectedId,
  });
  const selected = useMemo(
    () => accounts.data?.find((item) => item.id === selectedId),
    [accounts.data, selectedId],
  );
  const accountList = accounts.data ?? [];
  if (accounts.isLoading) return <ActivityIndicator accessibilityLabel="Carregando contas" />;
  if (accounts.isError || !selected)
    return <Text accessibilityRole="alert">Não foi possível carregar suas contas.</Text>;
  return (
    <ScrollView
      refreshControl={
        <RefreshControl
          refreshing={balance.isFetching || daily.isFetching}
          onRefresh={() => {
            balance.refetch();
            daily.refetch();
          }}
        />
      }
      contentContainerStyle={styles.container}
    >
      <Text accessibilityRole="header" style={styles.title}>
        Minha conta
      </Text>
      <Text>
        {selected.name} · agência {selected.branch} · conta {selected.number}
      </Text>
      <Text style={styles.status}>Status: {selected.status}</Text>
      <Text style={styles.balance}>R$ {(balance.data?.available ?? 0).toFixed(2)}</Text>
      <Text>Saldo disponível</Text>
      <Text>Saldo bloqueado: R$ {(balance.data?.blocked ?? 0).toFixed(2)}</Text>
      <Button title="Ver extrato" onPress={() => navigation.navigate("Statement")} />
      <Button title="Dados cadastrais" onPress={() => navigation.navigate("Profile")} />
      <Text style={styles.subtitle}>Movimentações de hoje</Text>
      {daily.data?.slice(0, 5).map((item) => (
        <Text key={item.id}>
          {item.description}: R$ {item.amount.toFixed(2)}
        </Text>
      ))}
      {accountList.length > 1
        ? accountList.map((item) => (
            <Button key={item.id} title={`Usar ${item.name}`} onPress={() => setAccount(item.id)} />
          ))
        : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { gap: 12, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
  subtitle: { fontSize: 20, fontWeight: "600", marginTop: 12 },
  balance: { fontSize: 32, fontWeight: "700", marginTop: 20 },
  status: { textTransform: "capitalize" },
});
