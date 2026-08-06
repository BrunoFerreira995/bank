import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { Button, StyleSheet, Text, TextInput, View } from "react-native";
import {
  getServicesStatus,
  listFaqs,
  listNotifications,
  listTickets,
  markNotificationRead,
  createTicket,
  safeErrorMessage,
} from "@/core/operations/operations-api";
import { useNotificationStore } from "@/core/operations/notification-store";

export function OperationsScreen() {
  const [subject, setSubject] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const notifications = useQuery({
    queryKey: ["notifications"],
    queryFn: async () => {
      const items = await listNotifications();
      useNotificationStore.getState().setItems(items);
      return items;
    },
  });
  const faqs = useQuery({ queryKey: ["faqs"], queryFn: listFaqs });
  const tickets = useQuery({ queryKey: ["tickets"], queryFn: listTickets });
  const services = useQuery({ queryKey: ["services-status"], queryFn: getServicesStatus });
  async function openTicket() {
    try {
      await createTicket(subject, description);
      setMessage("Solicitação aberta.");
      setSubject("");
      setDescription("");
      tickets.refetch();
    } catch (error) {
      setMessage(safeErrorMessage(error));
    }
  }
  return (
    <View style={styles.container}>
      <Text accessibilityRole="header" style={styles.title}>
        Suporte e operação
      </Text>
      <Text>Notificações</Text>
      {notifications.data?.slice(0, 5).map((item) => (
        <Button
          key={item.id}
          title={`${item.readAt ? "✓ " : ""}${item.title}`}
          onPress={() => {
            useNotificationStore.getState().markRead(item.id);
            markNotificationRead(item.id);
          }}
        />
      ))}
      <Text>Status dos serviços</Text>
      {services.data?.map((service) => (
        <Text key={service.service}>
          {service.service}: {service.status}
        </Text>
      ))}
      <Text>FAQ: {faqs.data?.length ?? 0} artigos disponíveis</Text>
      <Text>
        Tickets abertos: {tickets.data?.filter((ticket) => ticket.status !== "CLOSED").length ?? 0}
      </Text>
      <TextInput placeholder="Assunto do chamado" value={subject} onChangeText={setSubject} />
      <TextInput
        multiline
        placeholder="Descreva o problema"
        value={description}
        onChangeText={setDescription}
      />
      <Button
        disabled={!subject || !description}
        title="Abrir chamado"
        onPress={() => openTicket()}
      />
      {message ? <Text accessibilityRole="alert">{message}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, gap: 12, padding: 24 },
  title: { fontSize: 28, fontWeight: "700" },
});
