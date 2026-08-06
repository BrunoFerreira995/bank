import { NavigationContainer, type LinkingOptions } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useEffect } from "react";
import { ActivityIndicator, StyleSheet, View } from "react-native";
import { useSessionStore } from "@/core/auth/session-store";
import { LoginScreen } from "@/screens/LoginScreen";
import { AccountDashboardScreen } from "@/screens/AccountDashboardScreen";
import { ProfileScreen } from "@/screens/ProfileScreen";
import { StatementScreen } from "@/screens/StatementScreen";
import { OnboardingScreen } from "@/screens/OnboardingScreen";

type RootStackParamList = {
  Login: undefined;
  Onboarding: undefined;
  Home: undefined;
  Statement: undefined;
  Profile: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
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
