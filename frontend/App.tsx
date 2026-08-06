import { AppProviders } from "@/app/AppProviders";
import { AppNavigator } from "@/navigation/AppNavigator";
import { StatusBar } from "react-native";

export default function App() {
  return (
    <AppProviders>
      <StatusBar barStyle="dark-content" />
      <AppNavigator />
    </AppProviders>
  );
}
