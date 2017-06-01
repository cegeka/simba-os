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
package org.simbasecurity.common.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ThriftDate {

    public static Date parse(String date) {
        if (date == null || date.isEmpty()) return null;
        return Date.from(ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant());
    }

    public static String format(Date date) {
        if (date == null) return null;
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }
}
