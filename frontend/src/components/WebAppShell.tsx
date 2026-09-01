import { Platform, ScrollView, StyleSheet, useWindowDimensions, View } from "react-native";
import { Button, Divider, IconButton, Surface, Text } from "react-native-paper";
import { useActiveAccount } from "@/core/account/account-store";
import { useSessionStore } from "@/core/auth/session-store";

type RouteName =
  | "Home"
  | "Statement"
  | "Pix"
  | "Payments"
  | "FinancialProducts"
  | "OpenFinance"
  | "AccountServices"
  | "Operations"
  | "Profile";

const links: Array<{ route: RouteName; label: string; icon: string }> = [
  { route: "Home", label: "Início", icon: "⌂" },
  { route: "Statement", label: "Extrato", icon: "↕" },
  { route: "Pix", label: "Pix", icon: "✦" },
  { route: "Payments", label: "Pagamentos", icon: "▤" },
  { route: "FinancialProducts", label: "Cartões e crédito", icon: "▣" },
  { route: "OpenFinance", label: "Open Finance", icon: "◎" },
  { route: "AccountServices", label: "Serviços", icon: "⚙" },
  { route: "Operations", label: "Suporte", icon: "?" },
  { route: "Profile", label: "Perfil", icon: "◉" },
];

export function WebAppShell({
  children,
  navigation,
  routeName,
}: {
  children: React.ReactNode;
  navigation: { navigate: (route: RouteName) => void };
  routeName: RouteName;
}) {
  const { width } = useWindowDimensions();
  const activeAccount = useActiveAccount((state) => state.accountId);
  const logout = useSessionStore((state) => state.logout);
  const isWeb = Platform.OS === "web";
  const isDesktop = isWeb && width >= 1024;
  const isCompact = isWeb && !isDesktop;
  const isPhone = isCompact && width < 600;

  if (!isWeb) return <>{children}</>;

  return (
    <View style={[styles.page, isCompact && styles.compactPage]}>
      {isDesktop ? <Surface elevation={1} style={styles.sidebar}>
        <Text variant="titleLarge" style={styles.brand}>celcoin</Text>
        <Text variant="labelMedium" style={styles.brandDetail}>Internet Banking</Text>
        <View style={styles.navigation}>
          {links.map((link) => <NavigationLink key={link.route} link={link} active={routeName === link.route} navigation={navigation} />)}
        </View>
        <Divider />
        <Button icon="↗" mode="text" style={styles.menuButton} onPress={() => navigation.navigate("Operations")}>Central de ajuda</Button>
        <Button icon="×" mode="text" textColor="#BA1A1A" style={styles.menuButton} onPress={() => logout()}>Encerrar sessão</Button>
      </Surface> : null}
      <View style={styles.main}>
        <Surface elevation={0} style={[styles.header, isCompact && styles.compactHeader]}>
          {isCompact ? <Text variant="titleMedium" style={styles.compactBrand}>celcoin</Text> : null}
          <View style={isPhone && styles.phoneAccount}>
            <Text variant="labelMedium" style={styles.headerLabel}>{isPhone ? "Conta" : "Conta selecionada"}</Text>
            <Text variant="titleSmall">{activeAccount ? `Conta ${activeAccount}` : "Carregando conta"}</Text>
          </View>
          <View style={styles.headerActions}>
            <IconButton icon="●" accessibilityLabel="Notificações" onPress={() => navigation.navigate("Operations")} />
            {isPhone ? <IconButton icon="◉" accessibilityLabel="Meu perfil" onPress={() => navigation.navigate("Profile")} /> : <Button compact mode="outlined" icon="◉" onPress={() => navigation.navigate("Profile")}>Meu perfil</Button>}
          </View>
        </Surface>
        {isCompact ? <Surface elevation={0} style={styles.compactNavigation}>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.compactNavigationContent}>
            {links.map((link) => <Button key={link.route} compact mode={routeName === link.route ? "contained" : "text"} onPress={() => navigation.navigate(link.route)}>{link.label}</Button>)}
            <Button compact mode="text" textColor="#BA1A1A" onPress={() => logout()}>Sair</Button>
          </ScrollView>
        </Surface> : null}
        <View style={styles.content}>{children}</View>
      </View>
    </View>
  );
}

function NavigationLink({ link, active, navigation }: { link: (typeof links)[number]; active: boolean; navigation: { navigate: (route: RouteName) => void } }) {
  return <Button compact icon={link.icon} mode={active ? "contained" : "text"} contentStyle={styles.menuButtonContent} labelStyle={styles.menuButtonLabel} style={styles.menuButton} onPress={() => navigation.navigate(link.route)}>{link.label}</Button>;
}

const styles = StyleSheet.create({
  page: { backgroundColor: "#F5F8F7", flex: 1, flexDirection: "row" },
  compactPage: { flexDirection: "column" },
  sidebar: { backgroundColor: "#FFFFFF", gap: 10, padding: 24, width: 264 },
  brand: { color: "#006C5B", fontWeight: "800" },
  brandDetail: { color: "#52605C", marginTop: -8 },
  navigation: { flex: 1, gap: 4, marginTop: 28 },
  menuButton: { alignItems: "flex-start", justifyContent: "flex-start" },
  menuButtonContent: { justifyContent: "flex-start" },
  menuButtonLabel: { textAlign: "left" },
  main: { flex: 1, minWidth: 0 },
  header: { alignItems: "center", borderBottomColor: "#DCE5E1", borderBottomWidth: 1, flexDirection: "row", justifyContent: "space-between", minHeight: 72, paddingHorizontal: 32 },
  headerLabel: { color: "#52605C" },
  headerActions: { alignItems: "center", flexDirection: "row", gap: 8 },
  compactHeader: { minHeight: 64, paddingHorizontal: 16 },
  phoneAccount: { maxWidth: 112 },
  compactBrand: { color: "#006C5B", fontWeight: "800", marginRight: "auto" },
  compactNavigation: { borderBottomColor: "#DCE5E1", borderBottomWidth: 1 },
  compactNavigationContent: { alignItems: "center", gap: 4, paddingHorizontal: 12, paddingVertical: 8 },
  content: { flex: 1, minWidth: 0 },
});
