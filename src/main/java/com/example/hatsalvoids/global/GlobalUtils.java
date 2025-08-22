package com.example.hatsalvoids.global;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Component
public class GlobalUtils {
    public static ZonedDateTime getZonedDateTime(String time, String zoneId) {
        try {
            return ZonedDateTime.parse(time);
        } catch (DateTimeParseException ex) {
            return ZonedDateTime.of(LocalDateTime.parse(time), ZoneId.of(zoneId));
        }
    }
}
