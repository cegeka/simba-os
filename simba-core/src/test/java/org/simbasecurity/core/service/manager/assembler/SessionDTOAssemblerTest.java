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
package org.simbasecurity.core.service.manager.assembler;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.SessionEntity;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserEntity;
import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.domain.validator.UserValidator;
import org.simbasecurity.core.service.manager.dto.SessionDTO;
import org.simbasecurity.test.LocatorTestCase;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class SessionDTOAssemblerTest extends LocatorTestCase {

    @Before
    public void setup() {
        implantMock(UserValidator.class);
        implantMock(ConfigurationService.class);
        implantMock(PasswordValidator.class);
    }

    @Test
    public void testAssembleSingleSession() {
        User user = new UserEntity("username");
        SSOToken ssoToken = new SSOToken("token");
        String clientIpAddress = "127.0.0.1";
        String hostServerName = "server name";
        Session session = new SessionEntity(user, ssoToken, clientIpAddress, hostServerName);

        SessionDTO sessionData = SessionDTOAssembler.assemble(session);

        assertNotNull(sessionData);
        assertEquals(user.getUserName(), sessionData.getUserName());
        assertEquals(ssoToken.getToken(), sessionData.getSsoToken());
        assertEquals(clientIpAddress, sessionData.getClientIpAddress());
        assertTrue(0 < sessionData.getCreationTime());
        assertNotNull(sessionData.getLastAccessTime());
    }

    @Test
    public void testAssembleMultipleSessions() {
        User user = new UserEntity("username");
        SSOToken ssoToken = new SSOToken("token");
        String clientIpAddress = "127.0.0.1";
        String hostServerName = "server name";
        Session session = new SessionEntity(user, ssoToken, clientIpAddress, hostServerName);

        Collection<SessionDTO> sessionDataList = SessionDTOAssembler.assemble(Arrays.asList(session));

        assertNotNull(sessionDataList);
        assertEquals(1, sessionDataList.size());

    }

}
