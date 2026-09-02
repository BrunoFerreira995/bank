package com.brunopedraca.celcoin.bff.v1.openfinance;

import com.brunopedraca.celcoin.bff.v1.identity.MobileAuthentication;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/** Owns user-visible Open Finance state; provider callbacks may advance PENDING flows. */
@RestController
@RequestMapping(path="/mobile/v1/open-finance", produces=MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix="mobile.bff", name="enabled", havingValue="true")
public class MobileOpenFinanceLifecycleController {
    private final OfFlowRepository flows; private final OfPaymentRepository payments;
    public MobileOpenFinanceLifecycleController(OfFlowRepository flows, OfPaymentRepository payments){this.flows=flows;this.payments=payments;}
    @GetMapping("/links") public List<Flow> links(){return flows.findByUserIdOrderByCreatedAtDesc(user()).stream().map(Flow::from).toList();}
    @PostMapping(path="/redirect-flows",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Flow redirect(@Valid @RequestBody RedirectRequest request){return Flow.from(flows.save(new OfFlow(user(),"REDIRECT",request.institutionId(),request.consentId(),"AWAITING_CALLBACK")));}
    @GetMapping("/flows/{id}") public Flow flow(@PathVariable UUID id){return Flow.from(flows.findByIdAndUserId(id,user()).orElseThrow(()->new IllegalArgumentException("Flow not found")));}
    @PostMapping(path="/bricks/sessions",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Flow brick(@Valid @RequestBody BrickRequest request){return Flow.from(flows.save(new OfFlow(user(),"BRICK_"+request.product(),null,null,"PENDING")));}
    @PostMapping(path="/payments/immediate",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Payment immediate(@RequestHeader("Idempotency-Key") @NotBlank String key,@Valid @RequestBody PaymentRequest request){return Payment.from(payments.save(new OfPayment(user(),"IMMEDIATE",request.consentId(),request.amount(),null,"PENDING")));}
    @PostMapping(path="/payments/scheduled",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Payment scheduled(@RequestHeader("Idempotency-Key") @NotBlank String key,@Valid @RequestBody ScheduledPaymentRequest request){return Payment.from(payments.save(new OfPayment(user(),"SCHEDULED",request.consentId(),request.amount(),request.scheduledFor(),"PENDING")));}
    @PostMapping(path="/payments/automatic",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Payment automatic(@RequestHeader("Idempotency-Key") @NotBlank String key,@Valid @RequestBody PaymentRequest request){return Payment.from(payments.save(new OfPayment(user(),"AUTOMATIC",request.consentId(),request.amount(),null,"PENDING")));}
    @GetMapping("/payments") public List<Payment> payments(){return payments.findByUserIdOrderByCreatedAtDesc(user()).stream().map(Payment::from).toList();}
    @GetMapping("/payments/{id}") public Payment payment(@PathVariable UUID id){return Payment.from(payments.findByIdAndUserId(id,user()).orElseThrow(()->new IllegalArgumentException("Payment not found")));}
    @PostMapping("/payments/{id}/cancel") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void cancel(@PathVariable UUID id){payments.findByIdAndUserId(id,user()).orElseThrow(()->new IllegalArgumentException("Payment not found")).cancel();}
    @PostMapping(path="/sweeping/transfers",consumes=MediaType.APPLICATION_JSON_VALUE) @ResponseStatus(HttpStatus.CREATED) @Transactional public Payment sweeping(@RequestHeader("Idempotency-Key") @NotBlank String key,@Valid @RequestBody SweepingRequest request){return Payment.from(payments.save(new OfPayment(user(),"SWEEPING",null,request.amount(),null,"PENDING")));}
    @GetMapping("/sweeping/transfers") public List<Payment> sweeping(){return payments.findByUserIdAndTypeOrderByCreatedAtDesc(user(),"SWEEPING").stream().map(Payment::from).toList();}
    private static UUID user(){return MobileAuthentication.requiredUserId();}
    public record RedirectRequest(@NotBlank String institutionId,@NotBlank String consentId){} public record BrickRequest(@NotBlank String product){} public record PaymentRequest(@NotBlank String consentId,BigDecimal amount,String beneficiary){} public record ScheduledPaymentRequest(@NotBlank String consentId,BigDecimal amount,String beneficiary,LocalDate scheduledFor){} public record SweepingRequest(@NotBlank String sourceAccount,@NotBlank String targetAccount,BigDecimal amount){}
    public record Flow(String id,String status,String type,OffsetDateTime expiresAt){static Flow from(OfFlow f){return new Flow(f.id.toString(),f.status,f.type,f.createdAt.plusHours(1));}} public record Payment(String id,BigDecimal amount,String status,LocalDate scheduledFor,OffsetDateTime createdAt){static Payment from(OfPayment p){return new Payment(p.id.toString(),p.amount,p.status,p.scheduledFor,p.createdAt);}}
}
@Entity @Table(name="mobile_open_finance_flow") class OfFlow {@Id UUID id;@Column(name="user_id")UUID userId;String type;@Column(name="institution_id")String institutionId;@Column(name="consent_id")String consentId;String status;@Column(name="created_at")OffsetDateTime createdAt;protected OfFlow(){} OfFlow(UUID u,String t,String i,String c,String s){id=UUID.randomUUID();userId=u;type=t;institutionId=i;consentId=c;status=s;createdAt=OffsetDateTime.now();}}
@Entity @Table(name="mobile_open_finance_payment") class OfPayment {@Id UUID id;@Column(name="user_id")UUID userId;String type;@Column(name="consent_id")String consentId;BigDecimal amount;@Column(name="scheduled_for")LocalDate scheduledFor;String status;@Column(name="created_at")OffsetDateTime createdAt;protected OfPayment(){} OfPayment(UUID u,String t,String c,BigDecimal a,LocalDate d,String s){id=UUID.randomUUID();userId=u;type=t;consentId=c;amount=a;scheduledFor=d;status=s;createdAt=OffsetDateTime.now();}void cancel(){if(!"CANCELLED".equals(status))status="CANCELLED";}}
interface OfFlowRepository extends JpaRepository<OfFlow,UUID>{List<OfFlow> findByUserIdOrderByCreatedAtDesc(UUID userId);java.util.Optional<OfFlow> findByIdAndUserId(UUID id,UUID userId);} interface OfPaymentRepository extends JpaRepository<OfPayment,UUID>{List<OfPayment> findByUserIdOrderByCreatedAtDesc(UUID userId);List<OfPayment> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId,String type);java.util.Optional<OfPayment> findByIdAndUserId(UUID id,UUID userId);}
