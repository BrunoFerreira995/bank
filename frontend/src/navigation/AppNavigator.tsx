import { NavigationContainer, type LinkingOptions } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import { useEffect } from "react";
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
};

const Stack = createStackNavigator<RootStackParamList>();
const linking: LinkingOptions<RootStackParamList> = {
  prefixes: ["celcoin://", "https://app.example.com"],
  config: { screens: { Login: "login", Onboarding: "onboarding", Home: "home" } },
};

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
    <NavigationContainer linking={linking}>
      <Stack.Navigator>
        {session ? (
          <>
            <Stack.Screen
              name="Home"
              component={AccountDashboardScreen}
              options={{ title: "Celcoin" }}
            />
            <Stack.Screen
              name="Statement"
              component={StatementScreen}
              options={{ title: "Extrato" }}
            />
            <Stack.Screen name="Profile" component={ProfileScreen} options={{ title: "Perfil" }} />
            <Stack.Screen name="Pix" component={PixScreen} options={{ title: "Pix" }} />
            <Stack.Screen
              name="Payments"
              component={PaymentsScreen}
              options={{ title: "Boletos e pagamentos" }}
            />
            <Stack.Screen
              name="FinancialProducts"
              component={FinancialProductsScreen}
              options={{ title: "Produtos financeiros" }}
            />
            <Stack.Screen
              name="OpenFinance"
              component={OpenFinanceScreen}
              options={{ title: "Open Finance" }}
            />
            <Stack.Screen
              name="Operations"
              component={OperationsScreen}
              options={{ title: "Suporte e operação" }}
            />
          </>
        ) : (
          <>
            <Stack.Screen name="Login" component={LoginScreen} options={{ title: "Entrar" }} />
            <Stack.Screen
              name="Onboarding"
              component={OnboardingScreen}
              options={{ title: "Abertura de conta" }}
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
