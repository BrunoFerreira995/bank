package com.brunopedraca.celcoin.bff.v1.products;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping(path="/mobile/v1/credit", produces=MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix="mobile.bff.features", name="credit", havingValue="true")
public class MobileCreditController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileCreditController(CelcoinClient client, MobileAccountAuthorizationService authorization){this.client=client;this.authorization=authorization;}
    @PostMapping(path="/simulations",consumes=MediaType.APPLICATION_JSON_VALUE) public Map<String,Object> simulate(@Valid @RequestBody Simulation request){authorization.requireRead(request.accountId());return client.credit().simulate(request.productId(),request.data());}
    @PostMapping(path="/proposals",consumes=MediaType.APPLICATION_JSON_VALUE) public Map<String,Object> proposal(@Valid @RequestBody Proposal request){authorization.requireRisk(request.accountId());return client.credit().createApplication(request.data());}
    public record Simulation(@NotBlank String accountId,@NotBlank String productId,Map<String,Object> data){} public record Proposal(@NotBlank String accountId,Map<String,Object> data){}
}
