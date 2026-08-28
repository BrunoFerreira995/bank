import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo } from "react";
import { RefreshControl, ScrollView, StyleSheet, View } from "react-native";
import { ActivityIndicator, Button, Card, Divider, Text } from "react-native-paper";
import { getBalance, getDailyMovements, listAccounts } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";
import { useSessionStore } from "@/core/auth/session-store";

export function AccountDashboardScreen({
  navigation,
}: {
  navigation: {
    navigate: (
      route:
        | "Statement"
        | "Profile"
        | "Pix"
        | "Payments"
        | "FinancialProducts"
        | "OpenFinance"
        | "Operations"
        | "AccountServices",
    ) => void;
  };
}) {
  const { accountId, setAccount } = useActiveAccount();
  const logout = useSessionStore((state) => state.logout);
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
  const accountList = useMemo(() => accounts.data ?? [], [accounts.data]);
  useEffect(() => {
    if (!accountId && accountList[0]) setAccount(accountList[0].id);
  }, [accountId, accountList, setAccount]);
  if (accounts.isLoading) return <ActivityIndicator accessibilityLabel="Carregando contas" />;
  if (accounts.isError || !selected)
    return (
      <View style={styles.error}>
        <Text accessibilityRole="alert" style={styles.errorText}>
          Não foi possível carregar suas contas.
        </Text>
        <Button mode="contained" onPress={() => accounts.refetch()}>
          Tentar novamente
        </Button>
      </View>
    );
  return (
    <ScrollView
      testID="account-dashboard"
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
      <Text accessibilityRole="header" variant="headlineMedium">
        Minha conta
      </Text>
      <Text style={styles.accountDetails}>
        {selected.name} · agência {selected.branch} · conta {selected.number}
      </Text>
      <Text style={styles.status}>Status: {selected.status}</Text>
      <Button mode="text" style={styles.logout} onPress={() => logout()}>
        Sair
      </Button>
      <Card mode="elevated">
        <Card.Content>
          <Text variant="labelLarge" style={styles.balanceLabel}>
            Saldo disponível
          </Text>
          {balance.isLoading ? (
            <ActivityIndicator accessibilityLabel="Carregando saldo" />
          ) : balance.isError ? (
            <Text accessibilityRole="alert" style={styles.errorText}>
              Não foi possível carregar o saldo.
            </Text>
          ) : (
            <Text variant="displaySmall" style={styles.balance}>
              R$ {(balance.data?.available ?? 0).toFixed(2)}
            </Text>
          )}
          <Text variant="bodyMedium" style={styles.secondaryText}>
            Saldo bloqueado: R$ {(balance.data?.blocked ?? 0).toFixed(2)}
          </Text>
        </Card.Content>
      </Card>
      <Text variant="titleMedium" style={styles.subtitle}>
        Acessos rápidos
      </Text>
      <View style={styles.actions}>
        <Button mode="contained" onPress={() => navigation.navigate("Pix")}>
          Pix
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("Statement")}>
          Ver extrato
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("Payments")}>
          Boletos e recargas
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("Profile")}>
          Dados cadastrais
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("AccountServices")}>
          Mais serviços da conta
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("FinancialProducts")}>
          Cartões, crédito e Escrow
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("OpenFinance")}>
          Open Finance
        </Button>
        <Button mode="outlined" onPress={() => navigation.navigate("Operations")}>
          Suporte e notificações
        </Button>
      </View>
      <Divider />
      <Text variant="titleMedium" style={styles.subtitle}>
        Movimentações de hoje
      </Text>
      {daily.data?.slice(0, 5).map((item) => (
        <Text key={item.id} style={styles.movement}>
          {item.description}: R$ {item.amount.toFixed(2)}
        </Text>
      ))}
      {accountList.length > 1
        ? accountList.map((item) => (
            <Button key={item.id} mode="text" onPress={() => setAccount(item.id)}>
              Usar {item.name}
            </Button>
          ))
        : null}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { gap: 16, padding: 24 },
  subtitle: { marginTop: 8 },
  accountDetails: { color: "#374151" },
  status: { color: "#4b5563", textTransform: "capitalize" },
  balance: { color: "#006C5B", fontWeight: "700", marginVertical: 8 },
  balanceLabel: { color: "#374151", fontWeight: "600" },
  secondaryText: { color: "#6b7280" },
  movement: { color: "#374151" },
  actions: { gap: 8 },
  logout: { alignSelf: "flex-start" },
  error: { alignItems: "center", flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  errorText: { color: "#b91c1c" },
});
