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

import static java.util.Calendar.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final String HOUR_VALIDATION_REGEX = "([01][0-9]|2[0-3]):[0-5][0-9]";
    private static String HOUR_FORMAT = "HH:mm";
    private static SimpleDateFormat sdfHour;

    static {
        sdfHour = new SimpleDateFormat(HOUR_FORMAT);
    }

    private DateUtils() {
    }

    public static String getCurrentHour() {
        return sdfHour.format(new Date());
    }

    /**
     * @param target hour to check
     * @param start  interval start
     * @param end    interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        if (!validateHour(target)) {
            throw new IllegalArgumentException("Please specify a valid target hour");
        }

        if (!validateHour(start)) {
            throw new IllegalArgumentException("Please specify a valid start hour");
        }

        if (!validateHour(end)) {
            throw new IllegalArgumentException("Please specify a valid end hour");
        }

        if (start.compareTo(end) <= 0) {
            return ((target.compareTo(start) >= 0)
                    && (target.compareTo(end) <= 0));
        }

        return ((target.compareTo(start) <= 0)
                && (target.compareTo(end) >= 0));
    }

    /**
     * @param start interval start
     * @param end   interval end
     * @return true    true if the current hour is between
     */
    public static boolean isNowInInterval(String start, String end) {
        return isHourInInterval
                (getCurrentHour(), start, end);
    }

    public static boolean validateHour(String hour) {
        return hour != null && hour.matches(HOUR_VALIDATION_REGEX);
    }

    public static int getCurrentDayOfWeek() {
        Calendar cal = GregorianCalendar.getInstance();
        return cal.get(DAY_OF_WEEK);
    }
}
