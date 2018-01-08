package org.simbasecurity.core.util.dates;

import org.junit.rules.ExternalResource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.simbasecurity.core.util.dates.DateUtils.on;

public class SystemDate extends ExternalResource {

    private final StoredDate timeAtTestStart;
    private Consumer<LocalDateTime> consumer;

    public SystemDate() {
        this(null);
    }

    public SystemDate(int day, int month, int year) {
        this(day, month, year, 0, 0, 0);
    }

    public SystemDate(int day, int month, int year, int hour, int minute, int second) {
        this(new StoredDate(day, month, year, hour, minute, second));
    }

    private SystemDate(StoredDate storedDate) {
        this.timeAtTestStart = storedDate;
    }

    public void freeze(LocalDate date) {
        Time.freezeTime(date);
        if (consumer != null) consumer.accept(date.atStartOfDay());
    }

    public void freeze(LocalDateTime dateTime) {
        Time.freezeTime(dateTime);
        if (consumer != null) consumer.accept(dateTime);
    }

    public void freeze(int day, int month, int year) {
        this.freeze(day, month, year, 0, 0, 0);
    }

    public void freeze(int day, int month, int year, int hour, int minute, int second) {
        LocalDateTime date = on(year, month, day, hour, minute, second);
        Time.freezeTime(date);
        if (consumer != null) consumer.accept(date);
    }

    public void reset() {
        Time.clearBias();
        if (consumer != null) consumer.accept(DateUtils.now());
    }

    @Override
    protected void before() throws Throwable {
        if (timeAtTestStart != null) {
            freeze(timeAtTestStart.day, timeAtTestStart.month, timeAtTestStart.year, timeAtTestStart.hour, timeAtTestStart.minute, timeAtTestStart.second);
        }
    }

    @Override
    protected void after() {
        reset();
    }

    private static class StoredDate {
        private final int day;
        private final int month;
        private final int year;
        private final int hour;
        private final int minute;
        private final int second;

        private StoredDate(int day, int month, int year, int hour, int minute, int second) {
            this.day = day;
            this.month = month;
            this.year = year;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        private LocalDateTime toLocalDateTime() {
            return on(year, month, day, hour, minute, second);
        }

        private LocalDate toLocalDate() {
            return on(year, month, day, hour, minute, second).toLocalDate();
        }
    }
}