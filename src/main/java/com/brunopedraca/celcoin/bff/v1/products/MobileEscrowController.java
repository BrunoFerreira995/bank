package com.brunopedraca.celcoin.bff.v1.products;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping(path="/mobile/v1/escrow", produces=MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix="mobile.bff.features", name="escrow", havingValue="true")
public class MobileEscrowController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileEscrowController(CelcoinClient client, MobileAccountAuthorizationService authorization){this.client=client;this.authorization=authorization;}
    @GetMapping("/{accountId}") public Map<String,Object> account(@PathVariable String accountId){authorization.requireRead(accountId);return client.escrow().getAccount(accountId);}
    @GetMapping("/{accountId}/balance") public Map<String,Object> balance(@PathVariable String accountId){authorization.requireRead(accountId);return client.escrow().getBalance(accountId);}
    @GetMapping("/{accountId}/statement") public Map<String,Object> statement(@PathVariable String accountId,@RequestParam Map<String,Object> filters){authorization.requireRead(accountId);return client.escrow().getStatement(accountId,filters);}
}
