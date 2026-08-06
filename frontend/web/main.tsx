import { AppProviders } from "@/app/AppProviders";
import { AppNavigator } from "@/navigation/AppNavigator";
import { StatusBar } from "react-native";
import { createRoot } from "react-dom/client";
import "./styles.css";

createRoot(document.getElementById("root")!).render(
  <AppProviders>
    <StatusBar barStyle="dark-content" />
    <AppNavigator />
  </AppProviders>,
);
