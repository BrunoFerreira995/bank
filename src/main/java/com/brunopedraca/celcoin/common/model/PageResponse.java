package com.brunopedraca.celcoin.common.model;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, boolean hasNext) {}
