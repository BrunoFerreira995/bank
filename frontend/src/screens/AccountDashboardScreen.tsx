import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo, useState } from "react";
import { RefreshControl, ScrollView, StyleSheet, useWindowDimensions, View } from "react-native";
import { ActivityIndicator, Button, Card, Divider, IconButton, Text } from "react-native-paper";
import { getBalance, getDailyMovements, listAccounts } from "@/core/account/account-api";
import { useActiveAccount } from "@/core/account/account-store";
import { formatCurrency } from "@/utils/format";

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
  const { width } = useWindowDimensions();
  const isNarrow = width < 600;
  const { accountId, setAccount } = useActiveAccount();
  const [balanceVisible, setBalanceVisible] = useState(true);
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
      <View style={styles.intro}>
        <View>
          <Text accessibilityRole="header" variant="headlineMedium">Olá, {selected.name}</Text>
          <Text style={styles.accountDetails}>Agência {selected.branch} · conta {selected.number}</Text>
        </View>
        <Text style={styles.status}>Conta {selected.status.toLowerCase()}</Text>
      </View>
      <View style={[styles.dashboardGrid, isNarrow && styles.dashboardGridNarrow]}>
      <Card mode="elevated" style={styles.balanceCard}>
        <Card.Content>
          <View style={styles.balanceHeader}>
            <Text variant="labelLarge" style={styles.balanceLabel}>Saldo disponível</Text>
            <IconButton
              icon={balanceVisible ? "◉" : "○"}
              accessibilityLabel={balanceVisible ? "Ocultar saldo" : "Exibir saldo"}
              onPress={() => setBalanceVisible((visible) => !visible)}
            />
          </View>
          {balance.isLoading ? (
            <ActivityIndicator accessibilityLabel="Carregando saldo" />
          ) : balance.isError ? (
            <Text accessibilityRole="alert" style={styles.errorText}>
              Não foi possível carregar o saldo.
            </Text>
          ) : (
            <Text variant="displaySmall" style={styles.balance}>{balanceVisible ? formatCurrency(balance.data?.available) : "••••••"}</Text>
          )}
          <Text variant="bodyMedium" style={styles.secondaryText}>
            Saldo bloqueado: {balanceVisible ? formatCurrency(balance.data?.blocked) : "••••••"}
          </Text>
        </Card.Content>
      </Card>
      <Card mode="outlined" style={styles.noticeCard}>
        <Card.Content style={styles.noticeContent}>
          <Text variant="titleMedium">Tudo em ordem</Text>
          <Text variant="bodyMedium">Não há pendências para sua conta hoje.</Text>
          <Button compact mode="text" onPress={() => navigation.navigate("Operations")}>Ver notificações</Button>
        </Card.Content>
      </Card>
      </View>
      <Text variant="titleMedium" style={styles.subtitle}>
        Acessos rápidos
      </Text>
      <View style={styles.actions}>
        {[
          ["Pix", "✦", "Pix"], ["Statement", "↕", "Extrato"], ["Payments", "▤", "Pagamentos"], ["FinancialProducts", "▣", "Cartões e crédito"],
          ["AccountServices", "⚙", "Serviços"], ["OpenFinance", "◎", "Open Finance"], ["Operations", "?", "Suporte"], ["Profile", "◉", "Perfil"],
        ].map(([route, icon, label]) => (
          <Card key={route} mode="outlined" style={styles.actionCard} onPress={() => navigation.navigate(route as Parameters<typeof navigation.navigate>[0])}>
            <Card.Content style={styles.actionContent}>
              <Text style={styles.actionIcon}>{icon}</Text><Text variant="titleSmall">{label}</Text>
            </Card.Content>
          </Card>
        ))}
      </View>
      <Divider />
      <Text variant="titleMedium" style={styles.subtitle}>
        Movimentações de hoje
      </Text>
      <Card mode="outlined">
        <Card.Content style={styles.movements}>
          {daily.data?.slice(0, 5).map((item) => (
            <View key={item.id} style={styles.movementRow}><Text style={styles.movement}>{item.description}</Text><Text style={styles.movement}>{formatCurrency(item.amount)}</Text></View>
          ))}
          {!daily.data?.length ? <Text style={styles.secondaryText}>Nenhuma movimentação hoje.</Text> : null}
          <Button mode="text" onPress={() => navigation.navigate("Statement")}>Ver extrato completo</Button>
        </Card.Content>
      </Card>
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
  container: { alignSelf: "center", gap: 16, maxWidth: 1240, padding: 16, width: "100%" },
  intro: { alignItems: "flex-start", flexDirection: "row", flexWrap: "wrap", gap: 16, justifyContent: "space-between" },
  dashboardGrid: { flexDirection: "row", flexWrap: "wrap", gap: 16 },
  dashboardGridNarrow: { flexDirection: "column" },
  balanceCard: { flex: 2, minWidth: 0 },
  noticeCard: { flex: 1, minWidth: 0 },
  noticeContent: { gap: 6, justifyContent: "center" },
  subtitle: { marginTop: 8 },
  accountDetails: { color: "#374151" },
  status: { color: "#4b5563", textTransform: "capitalize" },
  balance: { color: "#006C5B", fontWeight: "700", marginVertical: 8 },
  balanceHeader: { alignItems: "center", flexDirection: "row", justifyContent: "space-between" },
  balanceLabel: { color: "#374151", fontWeight: "600" },
  secondaryText: { color: "#6b7280" },
  movement: { color: "#374151" },
  actions: { flexDirection: "row", flexWrap: "wrap", gap: 12 },
  actionCard: { flexBasis: 120, flexGrow: 1, minWidth: 0 },
  actionContent: { alignItems: "flex-start", gap: 8, minHeight: 82, justifyContent: "center" },
  actionIcon: { color: "#006C5B", fontSize: 22 },
  movements: { gap: 12 },
  movementRow: { flexDirection: "row", flexWrap: "wrap", gap: 4, justifyContent: "space-between" },
  error: { alignItems: "center", flex: 1, gap: 16, justifyContent: "center", padding: 24 },
  errorText: { color: "#b91c1c" },
});
