package com.brunopedraca.celcoin.bff.v1.pix;

import jakarta.validation.constraints.NotBlank;

/** Versioned input contract for decoding a Pix copy-and-paste payload. */
public record DecodePixRequest(@NotBlank String emv) {}
