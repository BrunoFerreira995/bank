package com.brunopedraca.celcoin.common.validation;

import java.util.regex.Pattern;

public final class SensitiveDataMasker {
    private static final Pattern CPF = Pattern.compile("\\b(\\d{3})\\.?\\d{3}\\.?\\d{3}-?(\\d{2})\\b");
    private static final Pattern CNPJ = Pattern.compile("\\b(\\d{2})\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?(\\d{2})\\b");
    private static final Pattern TOKEN =
            Pattern.compile("(?i)(access_token|client_secret|authorization)\"?\\s*[:=]\\s*\"?([^\",\\s]+)");

    private SensitiveDataMasker() {}

    public static String mask(String value) {
        if (value == null) {
            return null;
        }
        String masked = CPF.matcher(value).replaceAll("$1.***.***-$2");
        masked = CNPJ.matcher(masked).replaceAll("$1.***.***/****-$2");
        return TOKEN.matcher(masked).replaceAll("$1=***");
    }
}
