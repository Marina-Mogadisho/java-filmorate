package ru.yandex.practicum.filmorate.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilData {
    public static String format = "dd.MM.yyyy";

    public static LocalDateTime stringOfLocalData(String data) {

        if (data == null || data.isEmpty()) return null;
        else if (data.equals("0")) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(data, formatter);
        } catch (Exception e) {
            return null;
        }

    }

    public static Duration stringOfDuration(String duration) {
        if (duration == null || duration.isEmpty()) return null;
        else if (duration.equals("0")) return null;
        else {
            try {
                return Duration.ofMinutes(Long.parseLong(duration));
            } catch (Exception e) {
                return null;
            }
        }
    }
}
