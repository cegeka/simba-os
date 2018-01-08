package org.simbasecurity.core.util.dates;

import org.junit.After;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.util.Calendar.OCTOBER;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeTest {
    private static final LocalDateTime DATE_IN_THE_PAST = DateUtils.on(1970, OCTOBER, 13,12,11,10);

    @After
    public void tearDown() throws Exception {
        Time.clearBias();
    }

    @Test
    public void freezeTime() throws Exception {
        Time.freezeTime(DATE_IN_THE_PAST);
        sleep();
        assertThat(DateUtils.now()).isEqualTo(DATE_IN_THE_PAST);
    }

    @Test
    public void asDate_WorksAsNormalDateWhenBiasNotSet() throws Exception {
        LocalDateTime dateBeforeConstructor = LocalDateTime.now();
        sleep();
        LocalDateTime dateFromTimeInstance = DateUtils.now();
        sleep();
        LocalDateTime dateAfterConstructor = LocalDateTime.now();

        assertThat(dateBeforeConstructor).isBefore(dateFromTimeInstance);
        assertThat(dateAfterConstructor).isAfter(dateFromTimeInstance);
    }

    @Test
    public void clearBias() throws Exception {
        Time.freezeTime(DATE_IN_THE_PAST);
        Time.clearBias();
        LocalDateTime newLocalDateTime = LocalDateTime.now();
        LocalDateTime clearedDate = DateUtils.now();

        assertThat(newLocalDateTime).isBeforeOrEqualTo(clearedDate);
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(100);
    }
}