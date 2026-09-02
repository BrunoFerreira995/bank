package com.brunopedraca.celcoin.bff.v1.pix;

import java.util.Map;

/** Stable mobile representation; upstream additions remain isolated in the SDK. */
public record DecodePixResponse(String status, Map<String, Object> data) {}
