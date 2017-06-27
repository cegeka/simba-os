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
package org.simbasecurity.core.task;

import org.jasypt.digest.StringDigester;
import org.simbasecurity.core.audit.AuditLogIntegrityMessageFactory;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerifyAuditLogIntegrityTask implements QuartzTask {

    private static final Logger LOG = LoggerFactory.getLogger(VerifyAuditLogIntegrityTask.class);

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

    private static final String SELECT_FROM_SIMBA_ARCHIVED_AUDIT_LOG = "SELECT * FROM SIMBA_ARCHIVED_AUDIT_LOG";

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CoreConfigurationService configurationService;
    @Autowired private StringDigester digester;

    @Override
    public void execute() {
        if (!isAuditLogIntegrityEnabled()) {
            return;
        }
        RowMapper<VerifyAuditLogEvent> rowMapper = getRowMapper();
        List<VerifyAuditLogEvent> archivedAuditLogEvent = jdbcTemplate.query(SELECT_FROM_SIMBA_ARCHIVED_AUDIT_LOG,
                rowMapper);

        for (VerifyAuditLogEvent event : archivedAuditLogEvent) {
            String digestToCheck = digester.digest(AuditLogIntegrityMessageFactory.createMessage(event));
            String digestFromDb = event.getDigest();
            if (digestFromDb == null || digestToCheck == null || !digestFromDb.equals(digestToCheck)) {
                throw new AuditLogTamperedException(
                        "Archived Audit Log Integrity Check failed: calculated digest is not equal to the saved digest in the database");
            }
        }

        LOG.debug("Verified #" + archivedAuditLogEvent.size() + " archived audit log entries.");
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

    private boolean isAuditLogIntegrityEnabled() {
        return (Boolean) configurationService.getValue(SimbaConfigurationParameter.AUDIT_LOG_INTEGRITY_ENABLED);
    }
}
