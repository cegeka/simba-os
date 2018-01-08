package org.simbasecurity.core.util.dates;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

final class Time {

    static final ZoneOffset ZONE_OFF_SET = ZoneOffset.UTC;

    private static DateTimeStrategy strategy = new SystemTimeDTStrategy();

    static void freezeTime(LocalDate localDate) {
        strategy = Freeze.at(localDate);
    }

    static void freezeTime(LocalDateTime time) {
        strategy = Freeze.at(time);
    }

    static void clearBias() {
        strategy = new SystemTimeDTStrategy();
    }

    static LocalDate asLocalDate() {
        return strategy.getLocalDateTime().toLocalDate();
    }

    static LocalDateTime asLocalDateTime() {
        return strategy.getLocalDateTime();
    }

    public static <T> T doOn(LocalDateTime localDateTime, Supplier<T> supplier) {
        DateTimeStrategy old = Time.strategy;
        Time.strategy = (localDateTime != null) ? Freeze.at(localDateTime) : old;
        try {
            return supplier.get();
        } finally {
            Time.strategy = old;
        }
    }

}
