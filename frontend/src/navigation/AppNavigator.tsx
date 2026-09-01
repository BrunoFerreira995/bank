import { NavigationContainer, type LinkingOptions } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import { useEffect, type ComponentType } from "react";
import { ActivityIndicator, StyleSheet, View } from "react-native";
import { useSessionStore } from "@/core/auth/session-store";
import { LoginScreen } from "@/screens/LoginScreen";
import { AccountDashboardScreen } from "@/screens/AccountDashboardScreen";
import { ProfileScreen } from "@/screens/ProfileScreen";
import { StatementScreen } from "@/screens/StatementScreen";
import { PixScreen } from "@/screens/PixScreen";
import { PaymentsScreen } from "@/screens/PaymentsScreen";
import { FinancialProductsScreen } from "@/screens/FinancialProductsScreen";
import { OpenFinanceScreen } from "@/screens/OpenFinanceScreen";
import { OperationsScreen } from "@/screens/OperationsScreen";
import { OnboardingScreen } from "@/screens/OnboardingScreen";
import { AccountServicesScreen } from "@/screens/AccountServicesScreen";
import { PixManagementScreen } from "@/screens/PixManagementScreen";
import { WebAppShell } from "@/components/WebAppShell";
import { navigationTheme } from "@/ui/theme";

type RootStackParamList = {
  Login: undefined;
  Onboarding: undefined;
  Home: undefined;
  Statement: undefined;
  Profile: undefined;
  Pix: undefined;
  Payments: undefined;
  FinancialProducts: undefined;
  OpenFinance: undefined;
  Operations: undefined;
  AccountServices: undefined;
  PixManagement: undefined;
};

const Stack = createStackNavigator<RootStackParamList>();
const linking: LinkingOptions<RootStackParamList> = {
  prefixes: ["celcoin://", "https://app.example.com"],
  config: {
    screens: {
      Login: "login",
      Onboarding: "onboarding",
      Home: "home",
      Statement: "extrato",
      Profile: "perfil",
      Pix: "pix",
      Payments: "pagamentos",
      FinancialProducts: "produtos",
      OpenFinance: "open-finance",
      Operations: "suporte",
      AccountServices: "servicos",
      PixManagement: "pix/chaves",
    },
  },
};

function withWebShell<RouteName extends keyof RootStackParamList>(
  Screen: ComponentType<any>,
  routeName: Exclude<RouteName, "Login" | "Onboarding" | "PixManagement">,
) {
  return function ScreenWithWebShell(props: any) {
    return (
      <WebAppShell navigation={props.navigation} routeName={routeName}>
        <Screen {...props} />
      </WebAppShell>
    );
  };
}

export function AppNavigator() {
  const { hydrated, session, restore } = useSessionStore();
  useEffect(() => {
    restore();
  }, [restore]);

  if (!hydrated) {
    return (
      <View accessibilityLabel="Carregando sessão" style={styles.loading}>
        <ActivityIndicator />
      </View>
    );
  }

  return (
    <NavigationContainer linking={linking} theme={navigationTheme}>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {session ? (
          <>
            <Stack.Screen
              name="Home"
              component={withWebShell(AccountDashboardScreen, "Home")}
            />
            <Stack.Screen
              name="Statement"
              component={withWebShell(StatementScreen, "Statement")}
            />
            <Stack.Screen name="Profile" component={withWebShell(ProfileScreen, "Profile")} />
            <Stack.Screen name="Pix" component={withWebShell(PixScreen, "Pix")} />
            <Stack.Screen
              name="PixManagement"
              component={withWebShell(PixManagementScreen, "Pix")}
            />
            <Stack.Screen
              name="Payments"
              component={withWebShell(PaymentsScreen, "Payments")}
            />
            <Stack.Screen
              name="FinancialProducts"
              component={withWebShell(FinancialProductsScreen, "FinancialProducts")}
            />
            <Stack.Screen
              name="OpenFinance"
              component={withWebShell(OpenFinanceScreen, "OpenFinance")}
            />
            <Stack.Screen
              name="Operations"
              component={withWebShell(OperationsScreen, "Operations")}
            />
            <Stack.Screen
              name="AccountServices"
              component={withWebShell(AccountServicesScreen, "AccountServices")}
            />
          </>
        ) : (
          <>
            <Stack.Screen name="Login" component={LoginScreen} />
            <Stack.Screen
              name="Onboarding"
              component={OnboardingScreen}
            />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  loading: { flex: 1, alignItems: "center", justifyContent: "center" },
});
