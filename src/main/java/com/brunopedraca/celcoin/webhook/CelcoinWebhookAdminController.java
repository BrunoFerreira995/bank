package com.brunopedraca.celcoin.webhook;

import com.brunopedraca.celcoin.bff.audit.MobileAuditService;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAdministrationAuthorizationService;
import com.brunopedraca.celcoin.webhook.WebhookDtos.CelcoinWebhookEventResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@ConditionalOnProperty(prefix = "mobile.bff.admin", name = "enabled", havingValue = "true")
public class CelcoinWebhookAdminController {
    private final CelcoinWebhookService service; private final MobileAdministrationAuthorizationService administration; private final MobileAuditService audit;
    public CelcoinWebhookAdminController(CelcoinWebhookService service, MobileAdministrationAuthorizationService administration, MobileAuditService audit) { this.service = service; this.administration = administration; this.audit = audit; }
    @GetMapping("/admin/webhooks") public List<CelcoinWebhookEventResponse> list(@RequestHeader("Authorization") String authorization, @RequestHeader(value="X-Correlation-Id",required=false) String correlationId) { administration.requireAdministrator(authorization); long started=System.nanoTime(); try{return service.listEvents();}finally{audit.record("GET","/admin/webhooks",200,correlationId==null?"admin":correlationId,(System.nanoTime()-started)/1_000_000);}}
    @PostMapping("/admin/webhooks/{id}/retry") public CelcoinWebhookEventResponse retry(@PathVariable UUID id,@RequestHeader("Authorization") String authorization,@RequestHeader(value="X-Correlation-Id",required=false) String correlationId) { administration.requireAdministrator(authorization);long started=System.nanoTime();try{return service.retry(id);}finally{audit.record("POST","/admin/webhooks/"+id+"/retry",200,correlationId==null?"admin":correlationId,(System.nanoTime()-started)/1_000_000);}}
}
