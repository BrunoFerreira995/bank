package com.brunopedraca.celcoin.common.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PageRequest(@PositiveOrZero int page, @Positive int size) {
    public static PageRequest first() {
        return new PageRequest(0, 50);
    }
}
