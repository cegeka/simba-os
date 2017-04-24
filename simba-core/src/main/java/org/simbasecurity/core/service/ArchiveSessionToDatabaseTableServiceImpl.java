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
