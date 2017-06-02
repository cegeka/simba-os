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
package org.simbasecurity.manager.service.rest.assembler;

import org.junit.Test;
import org.simbasecurity.api.service.thrift.TSession;
import org.simbasecurity.manager.service.rest.dto.SessionDTO;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionDTOAssemblerTest {

    @Test
    public void testAssembleSingleSession() {
        TSession session = new TSession();
        session.setUserName("username");
        session.setSsoToken("token");
        session.setClientIpAddress("127.0.0.1");
        session.setCreationTime(12345L);
        session.setLastAccessTime(23456L);

        SessionDTO sessionData = DTOAssembler.assemble(session);

        assertThat(sessionData).isNotNull();
        assertThat(session.getUserName()).isEqualTo("username");
        assertThat(session.getSsoToken()).isEqualTo("token");
        assertThat(session.getClientIpAddress()).isEqualTo("127.0.0.1");
        assertThat(session.getCreationTime()).isEqualTo(12345L);
        assertThat(session.getLastAccessTime()).isEqualTo(23456L);
    }

    @Test
    public void testAssembleMultipleSessions() {

        TSession session = new TSession();
        session.setUserName("username");
        session.setSsoToken("token");
        session.setClientIpAddress("127.0.0.1");
        session.setCreationTime(12345L);
        session.setLastAccessTime(23456L);

        Collection<SessionDTO> sessionDataList = DTOAssembler.list(Collections.singletonList(session));

        assertThat(sessionDataList).extracting(SessionDTO::getUserName).containsExactly("username");
        assertThat(sessionDataList).extracting(SessionDTO::getSsoToken).containsExactly("token");
        assertThat(sessionDataList).extracting(SessionDTO::getClientIpAddress).containsExactly("127.0.0.1");
        assertThat(sessionDataList).extracting(SessionDTO::getCreationTime).containsExactly(12345L);
        assertThat(sessionDataList).extracting(SessionDTO::getLastAccessTime).containsExactly(23456L);
    }

}
