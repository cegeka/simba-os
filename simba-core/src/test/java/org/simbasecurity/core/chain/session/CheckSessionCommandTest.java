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
package org.simbasecurity.core.chain.session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.SessionService;

@RunWith(MockitoJUnitRunner.class)
public class CheckSessionCommandTest {
    private static final String CLIENT_IP = "10.0.0.100";

    private static final SSOToken SSO_TOKEN = new SSOToken();

    @Mock private ChainContext contextMock;
    @Mock private SessionService sessionServiceMock;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private CheckSessionCommand command;

    @Test
    public void redirectWhenNoSSOToken() throws Exception {
        when(contextMock.getRequestSSOToken()).thenReturn(null);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectToLogin();
    }

    @Test
    public void redirectWhenNoSession() throws Exception {
        when(contextMock.getRequestSSOToken()).thenReturn(SSO_TOKEN);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        when(contextMock.getCurrentSession()).thenReturn(null);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectToLogin();

    }

    @Test
    public void redirectWhenInvalidSession() throws Exception {
        when(contextMock.getRequestSSOToken()).thenReturn(SSO_TOKEN);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        Session sessionMock = mock(Session.class);
        when(sessionMock.isExpired()).thenReturn(true);
        when(contextMock.getCurrentSession()).thenReturn(sessionMock);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectToLogin();

    }

    @Test
    public void tokenIsTakenFromRequestData_IfNoMappingKeyProvided() throws Exception {
        Session sessionMock = mock(Session.class);
        when(contextMock.isSsoTokenMappingKeyProvided()).thenReturn(false);
        when(contextMock.getCurrentSession()).thenReturn(sessionMock);

        command.execute(contextMock);

        verify(contextMock, times(2)).getRequestSSOToken();
    }

    @Test
    public void tokenIsTakenFromRequestData_IfNoCurrentSession() throws Exception {
        when(contextMock.isSsoTokenMappingKeyProvided()).thenReturn(true);
        when(contextMock.getCurrentSession()).thenReturn(null);

        command.execute(contextMock);

        verify(contextMock, times(2)).getRequestSSOToken();
    }

    @Test
    public void tokenIsTakenFromCurrentSession_IfMappingProvidedAndSessionExists() throws Exception {
        Session sessionMock = mock(Session.class);
        when(contextMock.isSsoTokenMappingKeyProvided()).thenReturn(true);
        when(contextMock.getCurrentSession()).thenReturn(sessionMock);

        command.execute(contextMock);

        verify(sessionMock).getSSOToken();
    }

}
