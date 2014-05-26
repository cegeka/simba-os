/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.core.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.SessionRepository;
import org.simbasecurity.core.domain.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceImplTest {

    private static final String USER_NAME = "userName";
    private static final SSOToken SSO_TOKEN = new SSOToken();
    private static final String REMOTE_IP = "10.0.0.1";

    @Mock private Audit audit;
    @Mock private UserRepository userRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private ArchiveSessionService archiveSessionService;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private SessionServiceImpl service;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @Test
    public void testPurgeExpiredSessions() {
        Session expiredSession = createSessionMock(true);
        Session unexpiredSession = createSessionMock(false);

        when(sessionRepository.findAll()).thenReturn(Arrays.asList(expiredSession, unexpiredSession));

        service.purgeExpiredSessions();

        verify(sessionRepository).remove(expiredSession);
        verify(sessionRepository, never()).remove(unexpiredSession);
        verify(audit).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.SESSION, resultAuditLogEvent.getCategory());
		assertEquals("Purged expired session", resultAuditLogEvent.getMessage());        
    }

    private Session createSessionMock(boolean expired) {
        User userMock = mock(User.class);
        when(userMock.getUserName()).thenReturn(USER_NAME);

        Session sessionMock = mock(Session.class);

        when(sessionMock.isExpired()).thenReturn(expired);
        when(sessionMock.getUser()).thenReturn(userMock);
        when(sessionMock.getClientIpAddress()).thenReturn(REMOTE_IP);
        when(sessionMock.getSSOToken()).thenReturn(SSO_TOKEN);

        return sessionMock;
    }
}
