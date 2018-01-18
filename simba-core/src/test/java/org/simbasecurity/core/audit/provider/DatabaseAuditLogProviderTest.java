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

import org.jasypt.digest.StandardStringDigester;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogLevel;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.DatabaseTestCase;
import org.simbasecurity.test.LocatorRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DatabaseAuditLogProviderTest extends DatabaseTestCase {

    @Rule
    public LocatorRule locatorRule = LocatorRule.locator();
    protected CoreConfigurationService configurationServiceMock;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DatabaseAuditLogProvider provider;

    @Before
    public void setUp() {
        provider = new DatabaseAuditLogProvider();
        provider.setJdbcTemplate(jdbcTemplate);
        provider.setIntegrityDigest(new StandardStringDigester());
        provider.setDatabaseTable("SIMBA_AUDIT_LOG");
        provider.setLevel(AuditLogLevel.ALL);
        setUpCommonLocatables();
    }

    public void setUpCommonLocatables() {
        locatorRule.implantMock(UserValidator.class);
        locatorRule.implantMock(PasswordValidator.class);

        configurationServiceMock = locatorRule.getCoreConfigurationService();
    }

    @Test
    public void auditEventIsPersisted_digestNotEnabled() {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.AUDIT_LOG_INTEGRITY_ENABLED)).thenReturn(
                Boolean.FALSE);

        SSOToken ssoToken = new SSOToken();
        AuditLogEvent event = new AuditLogEvent(AuditLogEventCategory.SESSION, "username", ssoToken, "remoteIP",
                "message", "userAgent", "hostServerName", "surname", "firstname", "requestURL", "CHAINID");
        provider.log(event);

        jdbcTemplate.query("SELECT * FROM SIMBA_AUDIT_LOG WHERE ssoToken=?", getRowMapper(true), ssoToken.getToken());
    }

    @Test
    public void auditEventIsPersisted_digestEnabled() {
        when(configurationServiceMock.getValue(SimbaConfigurationParameter.AUDIT_LOG_INTEGRITY_ENABLED)).thenReturn(
                Boolean.TRUE);

        SSOToken ssoToken = new SSOToken();
        AuditLogEvent event = new AuditLogEvent(AuditLogEventCategory.SESSION, "username", ssoToken, "remoteIP",
                "message", "userAgent", "hostServerName", "surname", "firstname", "requestURL", "CHAINID");

        provider.log(event);

        jdbcTemplate.query("SELECT * FROM SIMBA_AUDIT_LOG WHERE ssoToken=?", getRowMapper(false), ssoToken.getToken());
    }

    private RowMapper<Object> getRowMapper(final boolean digestNull) {
        RowMapper<Object> rowMapper = new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                assertEquals("username", rs.getString("username"));
                assertEquals("remoteIP", rs.getString("remote_ip"));
                assertEquals(AuditLogEventCategory.SESSION.name(), rs.getString("eventcategory"));
                assertEquals("hostServerName", rs.getString("hostservername"));
                assertEquals("surname", rs.getString("name"));
                assertEquals("userAgent", rs.getString("useragent"));
                if (digestNull) {
                    assertNull(rs.getString("digest"));
                } else {
                    assertNotNull(rs.getString("digest"));
                }
                assertEquals("requestURL", rs.getString("requesturl"));
                assertEquals("CHAINID", rs.getString("chainid"));

                return null;
            }
        };
        return rowMapper;
    }
}
