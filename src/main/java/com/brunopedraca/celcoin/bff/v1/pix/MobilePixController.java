package com.brunopedraca.celcoin.bff.v1.pix;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mobile/v1/pix", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
@Tag(name = "Mobile v1 - Pix")
public class MobilePixController {
    private final MobilePixService service;

    public MobilePixController(MobilePixService service) {
        this.service = service;
    }

    @PostMapping(path = "/decode", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Decode a Pix EMV payload for the mobile app")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payload decoded"),
        @ApiResponse(responseCode = "400", description = "Invalid payload"),
        @ApiResponse(responseCode = "502", description = "Celcoin unavailable")
    })
    public DecodePixResponse decode(@Valid @RequestBody DecodePixRequest request) {
        return service.decode(request);
    }
}
