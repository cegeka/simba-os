/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.audit.provider;


import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerAuditLogProvider implements AuditLogProvider {

    private static final Logger LOG = LoggerFactory.getLogger(Audit.class);

    @Override
    public void log(AuditLogEvent event) {
        LOG.info(String.format("%-12s - %-15s - %-36s - %s", event.getUsername(), event.getRemoteIP(), event.getSSOTokenString(), event.getMessage()));
    }
}
