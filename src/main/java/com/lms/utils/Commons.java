package com.lms.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class Commons {

    private Commons() {}

    public static String orElse(String input, String fallback) {
        return (input == null || input.isBlank()) ? fallback : input;
    }

    public static Float orElse(Float input, Float fallback) {
        return input != null ? input : fallback;
    }

    public static Date orElse(LocalDate input, Date fallback) {
        return input != null ? Date.from(input.atStartOfDay(ZoneId.systemDefault()).toInstant()) : fallback;
    }
}
