package com.nam.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String nowAsString() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(FORMATTER);
    }

    public static String getDateTimeAsString(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}