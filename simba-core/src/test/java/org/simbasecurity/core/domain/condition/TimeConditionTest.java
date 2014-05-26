/*
 * Copyright 2013 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.simbasecurity.core.domain.condition;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

public class TimeConditionTest {

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void timeConditionDayTest() {
        TimeCondition condition = new TimeCondition("0 0 7 ? * 2-6",
                "0 0 19 ? * 2-6");

        assertTrue(condition.isInInterval(toMillis("2010-11-08 12:00:00")));
        assertTrue(condition.isInInterval(toMillis("2010-11-12 12:00:00")));

        assertTrue(condition.isInInterval(toMillis("2010-11-08 07:00:00")));
        assertTrue(condition.isInInterval(toMillis("2010-11-08 19:00:00")));

        assertFalse(condition.isInInterval(toMillis("2010-11-08 06:59:59")));
        assertFalse(condition.isInInterval(toMillis("2010-11-08 19:00:01")));

        assertFalse(condition.isInInterval(toMillis("2010-11-07 12:00:00")));
        assertFalse(condition.isInInterval(toMillis("2010-11-13 12:00:00")));
    }

    @Test
    public void timeConditionNightTest() {
        TimeCondition condition = new TimeCondition("0 0 19 ? * 2-6",
                "0 0 4 ? * 3-7");

        assertTrue(condition.isInInterval(toMillis("2010-11-08 22:00:00")));
        assertTrue(condition.isInInterval(toMillis("2010-11-12 22:00:00")));

        assertTrue(condition.isInInterval(toMillis("2010-11-08 19:00:00")));
        assertTrue(condition.isInInterval(toMillis("2010-11-09 04:00:00")));

        assertFalse(condition.isInInterval(toMillis("2010-11-08 18:59:59")));
        assertFalse(condition.isInInterval(toMillis("2010-11-09 04:00:01")));

        assertTrue(condition.isInInterval(toMillis("2010-11-13 02:00:00")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentThrownWhenInvallidExpression() {
        new TimeCondition("0 0 7 * * 2-6", "0 0 19 ? * 2-6");
    }

    @Test
    public void expirationTimestamp() {
        TimeCondition condition = new TimeCondition("0 0 7 ? * 2-6", "0 0 19 ? * 2-6");

        long endCurrentValidWindow = toMillis("2010-11-08 19:00:00");
        long startNextValidWindow = toMillis("2010-11-09 07:00:00");

        assertEquals(condition.getExpirationTimestamp(toMillis("2010-11-08 12:00:00")), endCurrentValidWindow);

        assertEquals(condition.getExpirationTimestamp(toMillis("2010-11-08 07:00:00")), endCurrentValidWindow);
        assertEquals(condition.getExpirationTimestamp(toMillis("2010-11-08 19:00:00")), endCurrentValidWindow);

        assertEquals(condition.getExpirationTimestamp(toMillis("2010-11-09 06:59:59")), startNextValidWindow);
        assertEquals(condition.getExpirationTimestamp(toMillis("2010-11-08 19:00:01")), startNextValidWindow);
    }

    private static long toMillis(String formattedDate) {
        try {
            return DF.parse(formattedDate).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
