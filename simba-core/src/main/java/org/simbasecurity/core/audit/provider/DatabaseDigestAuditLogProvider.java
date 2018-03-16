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
import org.simbasecurity.core.task.AuditLogTamperedException;
import org.simbasecurity.core.task.VerifyAuditLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

public class DatabaseDigestAuditLogProvider implements AuditLogProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseDigestAuditLogProvider.class);

    private static final String CHAINID = "CHAINID";

    private static final String REQUESTURL = "REQUESTURL";

    private static final String DIGEST = "DIGEST";

    private static final String EVENTCATEGORY = "EVENTCATEGORY";

    private static final String HOSTSERVERNAME = "HOSTSERVERNAME";

    private static final String USERAGENT = "USERAGENT";

    private static final String FIRSTNAME = "FIRSTNAME";

    private static final String NAME = "NAME";

    private static final String MESSAGE = "MESSAGE";

    private static final String REMOTE_IP = "REMOTE_IP";

    private static final String SSOTOKEN = "SSOTOKEN";

    private static final String USERNAME = "USERNAME";

    private static final String TIME_STAMP = "TIME_STAMP";

    private static final String NOT_REGISTERED = "not registered";

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private StringDigester integrityDigest;

    private AuditLogLevel level = AuditLogLevel.ALL;
    private String insertStatement;
    private String verifyStatement;

    public void setDatabaseTable(String databaseTable) {
        this.insertStatement =
                MessageFormat.format("INSERT INTO {0} " +
                                "({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        databaseTable, TIME_STAMP, USERNAME, SSOTOKEN, REMOTE_IP, MESSAGE, NAME, FIRSTNAME, USERAGENT,
                        HOSTSERVERNAME, EVENTCATEGORY, DIGEST, REQUESTURL, CHAINID);
        this.verifyStatement =
                MessageFormat.format("SELECT {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13} FROM {0}",
                        databaseTable, TIME_STAMP, USERNAME, SSOTOKEN, REMOTE_IP, MESSAGE, NAME, FIRSTNAME, USERAGENT,
                        HOSTSERVERNAME, EVENTCATEGORY, DIGEST, REQUESTURL, CHAINID);
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

            String digest = integrityDigest.digest(AuditLogIntegrityMessageFactory.createDigest(event, ssoToken));

            jdbcTemplate.update(insertStatement,
                               event.getTimestamp(), username, ssoToken, remoteIp, event.getMessage(), event.getName(),
                               event.getFirstName(), event.getUserAgent(), event.getHostServerName(),
                               event.getCategory().name(), digest, event.getRequestURL(), event.getChainId());
        }
    }

    public void verifyDigest() {
        RowMapper<VerifyAuditLogEvent> rowMapper = getRowMapper();
        List<VerifyAuditLogEvent> archivedAuditLogEvent = jdbcTemplate.query(verifyStatement, rowMapper);

        for (VerifyAuditLogEvent event : archivedAuditLogEvent) {
            if (!integrityDigest.matches(AuditLogIntegrityMessageFactory.createMessage(event), event.getDigest())) {
                throw new AuditLogTamperedException(
                        "Archived Audit Log Integrity Check failed: calculated digest is not equal to the saved digest in the database");
            }
        }

        LOG.info("Verified #" + archivedAuditLogEvent.size() + " archived audit log entries.");
        System.out.println("Verified #" + archivedAuditLogEvent.size() + " archived audit log entries.");
    }

    private RowMapper<VerifyAuditLogEvent> getRowMapper() {
        return (rs, rowNum) -> {
            VerifyAuditLogEvent event = new VerifyAuditLogEvent();
            event.setTimestamp(rs.getLong(TIME_STAMP));
            event.setUserName(rs.getString(USERNAME));
            event.setSsoToken(rs.getString(SSOTOKEN));
            event.setRemoteIp(rs.getString(REMOTE_IP));
            event.setMessage(rs.getString(MESSAGE));
            event.setName(rs.getString(NAME));
            event.setFirstName(rs.getString(FIRSTNAME));
            event.setUserAgent(rs.getString(USERAGENT));
            event.setHost(rs.getString(HOSTSERVERNAME));
            event.setEventCategory(rs.getString(EVENTCATEGORY));
            event.setDigest(rs.getString(DIGEST));
            event.setRequestUrl(rs.getString(REQUESTURL));
            event.setChainId(rs.getString(CHAINID));
            return event;
        };

    }

    void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void setIntegrityDigest(StringDigester integrityDigest) {
        this.integrityDigest = integrityDigest;
    }
}
