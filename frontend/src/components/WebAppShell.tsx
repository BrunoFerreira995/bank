import { useState } from "react";
import { Platform, Pressable, StyleSheet, View } from "react-native";
import { Button, Divider, IconButton, Surface, Text } from "react-native-paper";
import { useActiveAccount } from "@/core/account/account-store";
import { useSessionStore } from "@/core/auth/session-store";
import { layout, useResponsiveLayout } from "@/ui/layout";

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
  const { isPhone, isDesktop } = useResponsiveLayout();
  const activeAccount = useActiveAccount((state) => state.accountId);
  const logout = useSessionStore((state) => state.logout);
  const [moreOpen, setMoreOpen] = useState(false);
  const isWeb = Platform.OS === "web";
  const isCompact = isWeb && !isDesktop;

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
        {isCompact && !isPhone ? <Surface elevation={0} style={styles.tabletNavigation}>
          <View style={styles.tabletNavigationContent}>
            {links.slice(0, 5).map((link) => <Button key={link.route} compact mode={routeName === link.route ? "contained" : "text"} onPress={() => navigation.navigate(link.route)}>{link.label}</Button>)}
            <Button compact mode={moreOpen ? "contained" : "outlined"} onPress={() => setMoreOpen((open) => !open)}>Mais</Button>
          </View>
          {moreOpen ? <View style={styles.tabletMoreMenu}>{links.slice(5).map((link) => <Button key={link.route} compact mode={routeName === link.route ? "contained" : "text"} onPress={() => { setMoreOpen(false); navigation.navigate(link.route); }}>{link.label}</Button>)}<Button compact mode="text" textColor="#BA1A1A" onPress={() => logout()}>Sair</Button></View> : null}
        </Surface> : null}
        <View style={styles.content}>{children}</View>
      </View>
      {isPhone ? <Surface elevation={3} style={styles.mobileNavigation}>
        {moreOpen ? <View style={styles.moreMenu}>
          {links.slice(4).map((link) => <Button key={link.route} mode={routeName === link.route ? "contained" : "text"} onPress={() => { setMoreOpen(false); navigation.navigate(link.route); }}>{link.label}</Button>)}
          <Button mode="text" textColor="#BA1A1A" onPress={() => logout()}>Encerrar sessão</Button>
        </View> : null}
        <View style={styles.mobileNavigationBar}>
          {links.slice(0, 4).map((link) => <MobileLink key={link.route} link={link} active={routeName === link.route} navigation={navigation} />)}
          <Pressable accessibilityRole="button" accessibilityLabel="Mais opções" accessibilityState={{ expanded: moreOpen }} style={[styles.mobileLink, moreOpen && styles.mobileLinkActive]} onPress={() => setMoreOpen((open) => !open)}><Text style={styles.mobileLinkIcon}>•••</Text><Text style={styles.mobileLinkLabel}>Mais</Text></Pressable>
        </View>
      </Surface> : null}
    </View>
  );
}

function MobileLink({ link, active, navigation }: { link: (typeof links)[number]; active: boolean; navigation: { navigate: (route: RouteName) => void } }) {
  return <Pressable accessibilityRole="button" accessibilityLabel={link.label} accessibilityState={{ selected: active }} style={[styles.mobileLink, active && styles.mobileLinkActive]} onPress={() => navigation.navigate(link.route)}><Text style={styles.mobileLinkIcon}>{link.icon}</Text><Text numberOfLines={1} style={styles.mobileLinkLabel}>{link.label}</Text></Pressable>;
}

function NavigationLink({ link, active, navigation }: { link: (typeof links)[number]; active: boolean; navigation: { navigate: (route: RouteName) => void } }) {
  return <Button compact icon={link.icon} mode={active ? "contained" : "text"} contentStyle={styles.menuButtonContent} labelStyle={styles.menuButtonLabel} style={styles.menuButton} onPress={() => navigation.navigate(link.route)}>{link.label}</Button>;
}

const styles = StyleSheet.create({
  page: { backgroundColor: "#F5F8F7", flex: 1, flexDirection: "row" },
  compactPage: { flexDirection: "column" },
  sidebar: { backgroundColor: "#FFFFFF", gap: 10, padding: 24, width: 280 },
  brand: { color: "#006C5B", fontWeight: "800" },
  brandDetail: { color: "#52605C", marginTop: -8 },
  navigation: { flex: 1, gap: 4, marginTop: 28 },
  menuButton: { alignItems: "flex-start", justifyContent: "flex-start" },
  menuButtonContent: { justifyContent: "flex-start" },
  menuButtonLabel: { textAlign: "left" },
  main: { flex: 1, minWidth: 0 },
  header: { alignItems: "center", borderBottomColor: "#DCE5E1", borderBottomWidth: 1, flexDirection: "row", gap: 16, justifyContent: "space-between", minHeight: 72, paddingHorizontal: 32 },
  headerLabel: { color: "#52605C" },
  headerActions: { alignItems: "center", flexDirection: "row", gap: 8 },
  compactHeader: { minHeight: 64, paddingHorizontal: 16 },
  phoneAccount: { maxWidth: 112 },
  compactBrand: { color: "#006C5B", fontWeight: "800", marginRight: "auto" },
  tabletNavigation: { borderBottomColor: "#DCE5E1", borderBottomWidth: 1 },
  tabletNavigationContent: { alignItems: "center", flexDirection: "row", flexWrap: "wrap", gap: 4, paddingHorizontal: 16, paddingVertical: 8 },
  tabletMoreMenu: { borderTopColor: "#DCE5E1", borderTopWidth: 1, flexDirection: "row", flexWrap: "wrap", gap: 4, paddingHorizontal: 16, paddingVertical: 8 },
  content: { flex: 1, minWidth: 0 },
  mobileNavigation: { backgroundColor: "#FFFFFF", borderTopColor: "#DCE5E1", borderTopWidth: 1 },
  mobileNavigationBar: { alignItems: "stretch", flexDirection: "row", justifyContent: "space-around", minHeight: layout.touchTarget + 20, paddingHorizontal: 4, paddingVertical: 4 },
  mobileLink: { alignItems: "center", borderRadius: 12, flex: 1, justifyContent: "center", minHeight: layout.touchTarget, paddingHorizontal: 2 },
  mobileLinkActive: { backgroundColor: "#D8F4EC" },
  mobileLinkIcon: { color: "#006C5B", fontSize: 18, fontWeight: "700" },
  mobileLinkLabel: { color: "#25312D", fontSize: 11, fontWeight: "600", maxWidth: 66 },
  moreMenu: { borderBottomColor: "#DCE5E1", borderBottomWidth: 1, flexDirection: "row", flexWrap: "wrap", gap: 4, padding: 12 },
});
