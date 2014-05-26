package org.simbasecurity.core.service;

import org.simbasecurity.core.domain.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("databaseSessionArchive")
public class ArchiveSessionToDatabaseTableServiceImpl implements
		ArchiveSessionService {

	private static final String INSERT = "INSERT INTO SIMBA_ARCHIVED_SESSION (CLIENTIPADDRESS, CREATIONTIME, LASTACCESSTIME, SSOTOKEN,USER_ID,HOSTSERVERNAME ) SELECT CLIENTIPADDRESS, CREATIONTIME, LASTACCESSTIME, SSOTOKEN,USER_ID,HOSTSERVERNAME FROM SIMBA_SESSION";

    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public void archive(Session session) {
		jdbcTemplate.update(INSERT);
	}

}
