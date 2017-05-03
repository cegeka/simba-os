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
package org.simbasecurity.core.domain.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.SessionEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionDatabaseRepositoryTest extends PersistenceTestCase {

    @Autowired
    private SessionDatabaseRepository sessionDatabaseRepository;

    @Test
    public void canFindBySSOToken() throws Exception {
        User user = new UserEntity("jos");
        SSOToken ssoToken = new SSOToken("eenSsoTokentje");
        SessionEntity session = new SessionEntity(user, ssoToken, "127.0.0.1", "192.168.1.1");

        persistAndRefresh(user, session);

        assertEquals(session, sessionDatabaseRepository.findBySSOToken(ssoToken));

    }
}
