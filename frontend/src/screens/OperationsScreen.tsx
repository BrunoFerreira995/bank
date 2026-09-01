import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";
import { Button, Card, Text, TextInput } from "react-native-paper";
import { createTicket, getServicesStatus, listFaqs, listNotifications, listTickets, markNotificationRead, safeErrorMessage } from "@/core/operations/operations-api";
import { useNotificationStore } from "@/core/operations/notification-store";
import { sharedStyles } from "@/ui/layout";

export function OperationsScreen() {
  const [subject, setSubject] = useState(""); const [description, setDescription] = useState(""); const [message, setMessage] = useState<string | null>(null);
  const notifications = useQuery({ queryKey: ["notifications"], queryFn: async () => { const items = await listNotifications(); useNotificationStore.getState().setItems(items); return items; } });
  const faqs = useQuery({ queryKey: ["faqs"], queryFn: listFaqs }); const tickets = useQuery({ queryKey: ["tickets"], queryFn: listTickets }); const services = useQuery({ queryKey: ["services-status"], queryFn: getServicesStatus });
  async function openTicket() { try { await createTicket(subject, description); setMessage("Solicitação aberta."); setSubject(""); setDescription(""); tickets.refetch(); } catch (error) { setMessage(safeErrorMessage(error)); } }
  return <ScrollView contentContainerStyle={sharedStyles.page} testID="operations-screen">
    <Text accessibilityRole="header" style={sharedStyles.title}>Suporte e operação</Text>
    <View style={styles.grid}>
      <Card mode="outlined" style={styles.card}><Card.Title title="Notificações" /><Card.Content style={styles.section}>{notifications.data?.length ? notifications.data.slice(0, 5).map((item) => <Button key={item.id} mode="text" icon={item.readAt ? "✓" : "●"} onPress={() => { useNotificationStore.getState().markRead(item.id); markNotificationRead(item.id); }}>{item.title}</Button>) : <Text style={sharedStyles.subtitle}>Nenhuma notificação recente.</Text>}</Card.Content></Card>
      <Card mode="outlined" style={styles.card}><Card.Title title="Status e ajuda" /><Card.Content style={styles.section}>{services.data?.map((service) => <Text key={service.service}>{service.service}: {service.status}</Text>)}<Text style={sharedStyles.subtitle}>FAQ: {faqs.data?.length ?? 0} artigos · Tickets abertos: {tickets.data?.filter((ticket) => ticket.status !== "CLOSED").length ?? 0}</Text></Card.Content></Card>
    </View>
    <Card mode="outlined"><Card.Title title="Abrir chamado" subtitle="Descreva o problema para que possamos ajudar." /><Card.Content style={styles.section}><TextInput accessibilityLabel="Assunto do chamado" label="Assunto do chamado" mode="outlined" value={subject} onChangeText={setSubject} /><TextInput accessibilityLabel="Descreva o problema" label="Descreva o problema" mode="outlined" multiline numberOfLines={5} value={description} onChangeText={setDescription} /><Button mode="contained" disabled={!subject.trim() || !description.trim()} onPress={openTicket}>Abrir chamado</Button>{message ? <Text accessibilityRole="alert" style={message === "Solicitação aberta." ? sharedStyles.message : sharedStyles.error}>{message}</Text> : null}</Card.Content></Card>
  </ScrollView>;
}
const styles = StyleSheet.create({ grid: { flexDirection: "row", flexWrap: "wrap", gap: 16 }, card: { flex: 1, minWidth: 260 }, section: { gap: 12 } });
