package org.simbasecurity.core.audit.provider;

import org.jasypt.digest.StandardStringDigester;
import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogLevel;
import org.simbasecurity.test.DatabaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseDigestAuditLogProviderTest extends DatabaseTestCase {

    @Autowired private JdbcTemplate jdbcTemplate;
    private DatabaseDigestAuditLogProvider provider;

    @Before
    public void setUp() {
        provider = new DatabaseDigestAuditLogProvider();
        provider.setJdbcTemplate(jdbcTemplate);
        provider.setIntegrityDigest(new StandardStringDigester());
        provider.setDatabaseTable("SIMBA_AUDIT_LOG");
        provider.setLevel(AuditLogLevel.ALL);
    }

    @Test
    public void auditEventIsPersisted() {

        SSOToken ssoToken = new SSOToken();
        AuditLogEvent event = new AuditLogEvent(AuditLogEventCategory.SESSION, "username", ssoToken, "remoteIP",
                "message", "userAgent", "hostServerName", "surname", "firstname", "requestURL", "CHAINID");

        provider.log(event);

        jdbcTemplate.query("SELECT * FROM SIMBA_AUDIT_LOG WHERE ssoToken=?", getRowMapper(), ssoToken.getToken());
    }

    private RowMapper<Object> getRowMapper() {
        return (rs, rowNum) -> {
            assertEquals("username", rs.getString("username"));
            assertEquals("remoteIP", rs.getString("remote_ip"));
            assertEquals(AuditLogEventCategory.SESSION.name(), rs.getString("eventcategory"));
            assertEquals("hostServerName", rs.getString("hostservername"));
            assertEquals("surname", rs.getString("name"));
            assertEquals("userAgent", rs.getString("useragent"));
            assertNotNull(rs.getString("digest"));
            assertEquals("requestURL", rs.getString("requesturl"));
            assertEquals("CHAINID", rs.getString("chainid"));
            return null;
        };
    }

}
