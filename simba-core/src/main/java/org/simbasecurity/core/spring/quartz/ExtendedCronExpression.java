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

/**
 *
 */
package org.simbasecurity.core.spring.quartz;

import org.quartz.CronExpression;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ExtendedCronExpression implements Serializable {

    private static final long serialVersionUID = 4400034885809646850L;

    private static final int MINUTE = 1;
    private static final int HOUR = 2;
    private static final int DAY_OF_MONTH = 3;
    private static final int MONTH = 4;
    private static final int DAY_OF_WEEK = 5;
    private static final int YEAR = 6;

    private final CronExpression delegate;

    public ExtendedCronExpression(String cronExpression) throws ParseException {
        delegate = new CronExpression(cronExpression);
    }

    public Date getTimeAfter(Date afterTime) {
        return delegate.getTimeAfter(afterTime);
    }

    public Date getTimeBefore(Date beforeTime) {
        Calendar cl = Calendar.getInstance(delegate.getTimeZone());

        // to match this
        Date nextFireTime = getTimeAfter(beforeTime);
        cl.setTime(nextFireTime);
        cl.add(Calendar.SECOND, -1);

        String[] expression = delegate.getCronExpression().split(" ");
        int increment = findIncrement(expression);

        switch (increment) {
            case -1:
                break;
            case MINUTE:
                cl.add(Calendar.MINUTE, -1);
                break;
            case HOUR:
                cl.add(Calendar.HOUR_OF_DAY, -1);
                break;
            case DAY_OF_MONTH:
            case DAY_OF_WEEK:
                cl.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case MONTH:
                cl.add(Calendar.MONTH, -1);
                break;
            case YEAR:
            default:
                cl.add(Calendar.YEAR, -1);
                break;

        }
        Date output = getTimeAfter(cl.getTime());
        Date tmp;
        boolean searching = true;

        while (searching) {
            tmp = getTimeAfter(output);
            if (tmp.equals(nextFireTime)) {
                searching = false;
            } else {
                output = getTimeAfter(output);
            }
        }
        return output;

    }

    private int findIncrement(String[] expression) {
        // * * * * * * *
        // [0]SEC [1]MIN [2]HOUR [3]DAYOFMONTH [4]MONTH [5]DAYOFWEEK [6]YEAR
        for (int i = 0; i < expression.length; i++) {
            if (expression[i].equals("*")) {
                return i;
            }
        }

        return YEAR;
    }

}