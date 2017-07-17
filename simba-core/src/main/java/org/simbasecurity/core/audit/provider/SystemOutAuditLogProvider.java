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
package org.simbasecurity.core.audit.provider;

import org.simbasecurity.core.audit.AuditLogEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemOutAuditLogProvider implements AuditLogProvider {

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void log(AuditLogEvent event) {
        Date date = new Date(event.getTimestamp());
        String dateString = DF.format(date);

        System.out.printf("%s - %-24s - %-20s - %-15s - %s - %s%n", dateString, event.getCategory(),
                event.getUsername(), event.getRemoteIP(), event.getSSOTokenString(), event.getMessage());
    }
}
