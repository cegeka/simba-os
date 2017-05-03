/*
 * Copyright 2013-2017 Simba Open Source
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
 *
 */
package org.simbasecurity.core.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class DateUtilsTest {

    private static final String VALID_HOUR_TARGET = "07:00";
    private static final String VALID_HOUR_TARGET_BAD = "09:00";
    private static final String VALID_HOUR_START = "06:00";
    private static final String VALID_HOUR_END = "08:00";
    private static final String VALID_HOUR_START_MIN = "00:00";
    private static final String VALID_HOUR_END_MAX = "23:59";
    private static final String INVALID_HOUR = "07:R0";

    @Test
    public void isNowInInterval() {
        assertTrue(DateUtils.isNowInInterval(VALID_HOUR_START_MIN, VALID_HOUR_END_MAX));
    }

    @Test
    public void isIntervalReversed() {
        assertTrue(DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_END, VALID_HOUR_START));
    }

    @Test
    public void isHourInInterval() {
        assertTrue(DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_START, VALID_HOUR_END));
    }

    @Test
    public void isHourInInterval_TargetEqualsStart() {
        assertTrue(DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_TARGET, VALID_HOUR_END));
    }

    @Test
    public void isHourInInterval_TargetEqualsEnd() {
        assertTrue(DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_START, VALID_HOUR_TARGET));
    }

    @Test
    public void isHourInInterval_SameValues() {
        assertTrue(DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_TARGET, VALID_HOUR_TARGET));
    }

    @Test
    public void isHourNotInInterval() {
        assertFalse(DateUtils.isHourInInterval(VALID_HOUR_TARGET_BAD, VALID_HOUR_START, VALID_HOUR_END));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHourInInterval_invalidEndHour() {
        DateUtils.isHourInInterval(VALID_HOUR_TARGET, VALID_HOUR_TARGET, INVALID_HOUR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHourInInterval_invalidTargetHour() {
        DateUtils.isHourInInterval(INVALID_HOUR, VALID_HOUR_TARGET, VALID_HOUR_TARGET);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHourInInterval_invalidStartHour() {
        DateUtils.isHourInInterval(VALID_HOUR_TARGET, INVALID_HOUR, VALID_HOUR_TARGET);
    }
}
