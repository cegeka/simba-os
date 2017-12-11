package org.simbasecurity.core.util.dates;


import java.time.LocalDate;
import java.time.LocalDateTime;

final class Freeze implements DateTimeStrategy {
    private LocalDateTime date;

    private Freeze(LocalDateTime localDateTime) {
        this.date = localDateTime;
    }

    static Freeze at(LocalDate date) {
        return new Freeze(date.atStartOfDay());
    }

    static Freeze at(LocalDateTime date) {
        return new Freeze(date);
    }

    @Override
    public LocalDateTime getLocalDateTime() {
        return date;
    }
}
