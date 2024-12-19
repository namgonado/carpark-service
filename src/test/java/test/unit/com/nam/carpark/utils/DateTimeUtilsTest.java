package test.unit.com.nam.carpark.utils;

import com.nam.carpark.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeUtilsTest {

    @Test
    public void testNowAsString() {
        String now = DateTimeUtils.nowAsString();
        assertNotNull(now);
        assertFalse(now.isEmpty());
    }

    @Test
    public void testNowAsDateTimeWithZoneString() {
        String nowWithZone = DateTimeUtils.nowAsDateTimeWithZoneString();
        assertNotNull(nowWithZone);
        assertFalse(nowWithZone.isEmpty());
    }

    @Test
    public void testGetDateTimeAsString() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 17, 12, 40, 27);
        String formattedDateTime = DateTimeUtils.getDateTimeAsString(dateTime);
        assertEquals("2024-12-17T12:40:27", formattedDateTime);
    }

    @Test
    public void testParseDateTimeWithZone() {
        String dateTimeStr = "2024-12-17T12:40:27+08:00";
        LocalDateTime parsedDateTime = DateTimeUtils.parseDateTimeWithZone(dateTimeStr);
        assertNotNull(parsedDateTime);
        assertEquals(LocalDateTime.of(2024, 12, 17, 12, 40, 27), parsedDateTime);
    }
}
