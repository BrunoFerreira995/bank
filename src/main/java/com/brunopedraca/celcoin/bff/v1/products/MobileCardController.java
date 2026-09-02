package com.brunopedraca.celcoin.bff.v1.products;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.bff.v1.identity.MobileAccountAuthorizationService;
import com.brunopedraca.celcoin.cards.CardDtos.CelcoinCardListRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping(path="/mobile/v1/cards", produces=MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix="mobile.bff.features", name="cards", havingValue="true")
public class MobileCardController {
    private final CelcoinClient client; private final MobileAccountAuthorizationService authorization;
    public MobileCardController(CelcoinClient client, MobileAccountAuthorizationService authorization){this.client=client;this.authorization=authorization;}
    @GetMapping public Object list(@RequestParam @NotBlank String accountId, @RequestParam @NotBlank String cardAccountId, @RequestParam(required=false) String status) { authorization.requireRead(accountId); return client.cards().listCards(new CelcoinCardListRequest(cardAccountId,status,null,0,50)); }
    @PostMapping("/{cardId}/activation") public Object activate(@PathVariable String cardId, @RequestParam @NotBlank String accountId, @RequestHeader("Idempotency-Key") String key) { authorization.requireRisk(accountId); return client.cards().activateCard(cardId,key); }
}
