package com.nam.carpark.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String nowAsString() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(FORMATTER);
    }

    public static String nowAsDateTimeWithZoneString() {
        OffsetDateTime now = OffsetDateTime.now();
        return now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    public static String getDateTimeAsString(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime parseDateTimeWithZone(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        return localDateTime;
    }

}