package com.lms.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;

public final class Commons {

    private Commons() {}

    public static String orElse(String input, String fallback) {
        return (input == null || input.isBlank()) ? fallback : input;
    }

    public static Float orElse(Float input, Float fallback) {
        return input != null ? input : fallback;
    }

    public static Date orElse(LocalDate input, Date fallback) {
        return input != null && !input.toString().isEmpty() ? Date.from(input.atStartOfDay(ZoneId.systemDefault()).toInstant()) : fallback;
    }

    public static Float readFloatOrElse(Scanner sc, Float fallback) {
        String input = sc.nextLine();
        return input.isBlank() ? fallback : Float.parseFloat(input);
    }

    public static Date addYears(Date date, int years) {
        return Date.from(
                date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .plusYears(years)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
