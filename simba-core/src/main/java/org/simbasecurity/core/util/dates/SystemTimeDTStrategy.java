package org.simbasecurity.core.util.dates;


import java.time.LocalDateTime;

final class SystemTimeDTStrategy implements DateTimeStrategy {

    @Override
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }
}
