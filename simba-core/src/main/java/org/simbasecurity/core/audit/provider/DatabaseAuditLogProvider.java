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

import org.jasypt.digest.StringDigester;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogIntegrityMessageFactory;
import org.simbasecurity.core.audit.AuditLogLevel;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DatabaseAuditLogProvider implements AuditLogProvider {

    private static final String NOT_REGISTERED = "not registered";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StringDigester integrityDigest;

    private AuditLogLevel level = AuditLogLevel.ALL;
    private String sqlStatement;

    public void setDatabaseTable(String databaseTable) {
        this. sqlStatement = "INSERT INTO " + databaseTable +
                       " (TIME_STAMP, USERNAME, SSOTOKEN, REMOTE_IP, MESSAGE, NAME, FIRSTNAME, USERAGENT, " +
                       "  HOSTSERVERNAME, EVENTCATEGORY, DIGEST, REQUESTURL, CHAINID) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    }

    public void setLevel(AuditLogLevel level) {
        this.level = level;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(AuditLogEvent event) {
        if (this.level.isLoggable(event.getAuditLogLevel())) {
            String ssoToken = event.getSSOToken() == null ? null : event.getSSOTokenString();

            String username = event.getUsername();
            if (username == null) {
                username = NOT_REGISTERED;
            }
            String remoteIp = event.getRemoteIP();
            if (remoteIp == null) {
                remoteIp = NOT_REGISTERED;
            }

            String digest = null;
            if (isAuditLogIntegrityEnabled()) {
                digest = integrityDigest.digest(AuditLogIntegrityMessageFactory.createDigest(event, ssoToken));
            }

            jdbcTemplate.update(sqlStatement,
                               event.getTimestamp(), username, ssoToken, remoteIp, event.getMessage(), event.getName(),
                               event.getFirstName(), event.getUserAgent(), event.getHostServerName(),
                               event.getCategory().name(), digest, event.getRequestURL(), event.getChainId());
        }
    }

    private boolean isAuditLogIntegrityEnabled() {
        return GlobalContext.locate(ConfigurationServiceImpl.class).<Boolean>getValue(SimbaConfigurationParameter.AUDIT_LOG_INTEGRITY_ENABLED);
    }

    void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void setIntegrityDigest(StringDigester integrityDigest) {
        this.integrityDigest = integrityDigest;
    }
}
