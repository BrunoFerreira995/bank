import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, View } from "react-native";
import { env } from "@/config/env";
import {
  createConsent,
  listConsents,
  listInstitutions,
  revokeConsent,
} from "@/core/open-finance/open-finance-api";

export function OpenFinanceScreen() {
  const [message, setMessage] = useState<string | null>(null);
  const institutions = useQuery({
    queryKey: ["open-finance-institutions"],
    queryFn: listInstitutions,
    enabled: env.flags.openFinance,
  });
  const consents = useQuery({
    queryKey: ["open-finance-consents"],
    queryFn: listConsents,
    enabled: env.flags.openFinance,
  });
  async function authorize(institutionId: string) {
    try {
      await createConsent(institutionId, ["ACCOUNTS", "TRANSACTIONS"]);
      setMessage("Consentimento criado. Continue na instituição selecionada.");
      consents.refetch();
    } catch {
      setMessage("Instituição indisponível ou consentimento recusado.");
    }
  }
  async function revoke(id: string) {
    await revokeConsent(id);
    consents.refetch();
  }
  if (!env.flags.openFinance) return <Text>Open Finance indisponível para este contrato.</Text>;
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Open Finance
      </Text>
      <Text>Instituições disponíveis</Text>
      {institutions.data?.map((institution) => (
        <Button
          key={institution.id}
          disabled={institution.status !== "AVAILABLE"}
          title={institution.name}
          onPress={() => authorize(institution.id)}
        />
      ))}
      <Text>Consentimentos e vínculos</Text>
      {consents.data?.map((consent) => (
        <View key={consent.id}>
          <Text>
            {consent.institutionName} · {consent.status}
          </Text>
          <Button title="Revogar" onPress={() => revoke(consent.id)} />
        </View>
      ))}
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 16, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
