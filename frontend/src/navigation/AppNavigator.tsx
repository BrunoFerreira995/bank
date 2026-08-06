import { NavigationContainer, type LinkingOptions } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useEffect } from "react";
import { ActivityIndicator, StyleSheet, View } from "react-native";
import { useSessionStore } from "@/core/auth/session-store";
import { LoginScreen } from "@/screens/LoginScreen";
import { HomeScreen } from "@/screens/HomeScreen";

type RootStackParamList = {
  Login: undefined;
  Home: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const linking: LinkingOptions<RootStackParamList> = {
  prefixes: ["celcoin://", "https://app.example.com"],
  config: { screens: { Login: "login", Home: "home" } },
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
          <Stack.Screen name="Home" component={HomeScreen} options={{ title: "Celcoin" }} />
        ) : (
          <Stack.Screen name="Login" component={LoginScreen} options={{ title: "Entrar" }} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  loading: { flex: 1, alignItems: "center", justifyContent: "center" },
});
