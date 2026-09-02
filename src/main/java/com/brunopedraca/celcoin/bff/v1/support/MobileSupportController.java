package com.brunopedraca.celcoin.bff.v1.support;

import com.brunopedraca.celcoin.bff.v1.identity.MobileAuthentication;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileSupportController {
    private final TicketRepository tickets; private final NotificationRepository notifications;
    public MobileSupportController(TicketRepository tickets, NotificationRepository notifications) { this.tickets = tickets; this.notifications = notifications; }
    @GetMapping(path = "/mobile/v1/support/tickets", produces = MediaType.APPLICATION_JSON_VALUE) public List<TicketResponse> tickets() { return tickets.findByUserIdOrderByUpdatedAtDesc(MobileAuthentication.requiredUserId()).stream().map(TicketResponse::from).toList(); }
    @PostMapping(path = "/mobile/v1/support/tickets", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public TicketResponse create(@Valid @RequestBody TicketRequest request) { UUID user = MobileAuthentication.requiredUserId(); Ticket ticket = tickets.save(new Ticket(user, request.subject(), request.description())); notifications.save(new Notification(user, "Solicitação aberta", "Seu ticket foi registrado.", "SUPPORT")); return TicketResponse.from(ticket); }
    @GetMapping(path = "/mobile/v1/support/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE) public TicketResponse ticket(@PathVariable UUID id) { return TicketResponse.from(tickets.findByIdAndUserId(id, MobileAuthentication.requiredUserId()).orElseThrow(() -> new IllegalArgumentException("Ticket not found"))); }
    @GetMapping(path = "/mobile/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE) public List<NotificationResponse> notifications() { return notifications.findByUserIdOrderByCreatedAtDesc(MobileAuthentication.requiredUserId()).stream().map(NotificationResponse::from).toList(); }
    @PostMapping("/mobile/v1/notifications/{id}/read") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void read(@PathVariable UUID id) { notifications.findByIdAndUserId(id, MobileAuthentication.requiredUserId()).orElseThrow(() -> new IllegalArgumentException("Notification not found")).read(); }
    @GetMapping(path = "/mobile/v1/support/faqs", produces = MediaType.APPLICATION_JSON_VALUE) public List<Faq> faqs() { return List.of(new Faq("security", "Como mantenho minha conta segura?", "Nunca compartilhe suas credenciais ou códigos MFA.", "SECURITY")); }
    @GetMapping(path = "/mobile/v1/operations/services", produces = MediaType.APPLICATION_JSON_VALUE) public List<Service> services() { return List.of(new Service("BFF", "OPERATIONAL", "Serviço disponível", OffsetDateTime.now())); }
    public record TicketRequest(@NotBlank String subject, @NotBlank String description) {} public record TicketResponse(String id, String subject, String status, OffsetDateTime updatedAt) { static TicketResponse from(Ticket t) { return new TicketResponse(t.id.toString(), t.subject, t.status, t.updatedAt); } } public record NotificationResponse(String id, String title, String body, String category, OffsetDateTime readAt, OffsetDateTime createdAt) { static NotificationResponse from(Notification n) { return new NotificationResponse(n.id.toString(),n.title,n.body,n.category,n.readAt,n.createdAt); } } public record Faq(String id,String question,String answer,String category) {} public record Service(String service,String status,String message,OffsetDateTime updatedAt) {}
}
@Entity @Table(name="mobile_ticket") class Ticket { @Id UUID id; @Column(name="user_id") UUID userId; String subject; @Column(length=4000) String description; String status; @Column(name="updated_at") OffsetDateTime updatedAt; protected Ticket(){} Ticket(UUID u,String s,String d){id=UUID.randomUUID();userId=u;subject=s;description=d;status="OPEN";updatedAt=OffsetDateTime.now();} }
@Entity @Table(name="mobile_notification") class Notification { @Id UUID id; @Column(name="user_id") UUID userId; String title; @Column(length=4000) String body; String category; @Column(name="read_at") OffsetDateTime readAt; @Column(name="created_at") OffsetDateTime createdAt; protected Notification(){} Notification(UUID u,String t,String b,String c){id=UUID.randomUUID();userId=u;title=t;body=b;category=c;createdAt=OffsetDateTime.now();} void read(){if(readAt==null)readAt=OffsetDateTime.now();} }
interface TicketRepository extends JpaRepository<Ticket,UUID>{List<Ticket> findByUserIdOrderByUpdatedAtDesc(UUID userId); java.util.Optional<Ticket> findByIdAndUserId(UUID id,UUID userId);} interface NotificationRepository extends JpaRepository<Notification,UUID>{List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId); java.util.Optional<Notification> findByIdAndUserId(UUID id,UUID userId);}
