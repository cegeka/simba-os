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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.User;

@RunWith(MockitoJUnitRunner.class)
public class CheckClientIPCommandTest {

    private static final String IP_ADDRESS = "192.168.1.1";
    private static final String OTHER_IP_ADDRESS = "10.0.0.1";

    @Mock private User userMock;
    @Mock private Session sessionMock;

    @Mock private ChainContext chainContextMock;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private CheckClientIPCommand command;

    @Before
    public void setUp() {
        when(sessionMock.getUser()).thenReturn(userMock);
        when(chainContextMock.getCurrentSession()).thenReturn(sessionMock);
        when(chainContextMock.getClientIpAddress()).thenReturn(IP_ADDRESS);
    }

    @Test
    public void redirectIfClientIpDifferentFromIpStoredInSession() throws Exception {
        when(sessionMock.getClientIpAddress()).thenReturn(OTHER_IP_ADDRESS);

        Command.State state = command.execute(chainContextMock);

        assertEquals(State.FINISH, state);
        verify(chainContextMock).redirectToLogin();
    }

    @Test
    public void continueWithoutActionsIfClientIpEqualsIpStoredInSession() throws Exception {
        when(sessionMock.getClientIpAddress()).thenReturn(IP_ADDRESS);

        State state = command.execute(chainContextMock);

        verify(chainContextMock, times(0)).activateAction(isA(ActionType.class));
        assertEquals(State.CONTINUE, state);
    }
}
