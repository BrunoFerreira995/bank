package com.brunopedraca.celcoin.bff.v1.pix;

import com.brunopedraca.celcoin.CelcoinClient;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixEmvDecodeResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class MobilePixService {
    private final CelcoinClient celcoinClient;
    private final ObjectMapper objectMapper;

    public MobilePixService(CelcoinClient celcoinClient, ObjectMapper objectMapper) {
        this.celcoinClient = celcoinClient;
        this.objectMapper = objectMapper;
    }

    public DecodePixResponse decode(DecodePixRequest request) {
        CelcoinPixEmvDecodeResponse decoded = celcoinClient.pix().decodeEmv(request.emv());
        return new DecodePixResponse(
                decoded.status(), objectMapper.convertValue(decoded.body(), new TypeReference<>() {}));
    }
}
