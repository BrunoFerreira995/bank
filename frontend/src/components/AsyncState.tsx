import { ActivityIndicator, Button, StyleSheet, Text, View } from "react-native";

export type AsyncStateKind = "loading" | "empty" | "error" | "success";

export function AsyncState({
  state,
  message,
  onRetry,
  children,
}: {
  state: AsyncStateKind;
  message?: string;
  onRetry?: () => void;
  children?: React.ReactNode;
}) {
  if (state === "success") return <>{children}</>;
  if (state === "loading")
    return (
      <View accessibilityLabel="Carregando" style={styles.container}>
        <ActivityIndicator />
        <Text>Carregando...</Text>
      </View>
    );
  if (state === "empty")
    return (
      <View accessibilityLabel="Sem resultados" style={styles.container}>
        <Text>{message ?? "Nenhum resultado encontrado."}</Text>
      </View>
    );
  return (
    <View accessibilityRole="alert" style={styles.container}>
      <Text>{message ?? "Não foi possível concluir a operação."}</Text>
      {onRetry ? <Button title="Tentar novamente" onPress={onRetry} /> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { alignItems: "center", gap: 12, justifyContent: "center", padding: 24 },
});
