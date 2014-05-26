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
package org.simbasecurity.core.chain.authentication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.CredentialService;


@RunWith(MockitoJUnitRunner.class)
public class CheckUserActiveCommandTest {

    private static final String USER_NAME = "userName";

    @Mock private CredentialService credentialServiceMock;
    @Mock private Audit auditMock;
    @Mock private ChainContext contextMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private CheckUserActiveCommand command;

    @Before
    public void setUp() throws Exception {
        when(contextMock.getUserName()).thenReturn(USER_NAME);
    }

    @Test
    public void testExecute_UserDoesntExist() throws Exception {
        when(credentialServiceMock.checkUserExists(USER_NAME)).thenReturn(false);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectWithCredentialError(SimbaMessageKey.LOGIN_FAILED);
    }

    @Test
    public void testExecute_UserNotInactive() throws Exception {
        when(credentialServiceMock.checkUserExists(USER_NAME)).thenReturn(true);
        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.INACTIVE)).thenReturn(false);

        assertEquals(State.CONTINUE, command.execute(contextMock));

        verify(contextMock, never()).redirectWithCredentialError(SimbaMessageKey.LOGIN_FAILED);
    }

    @Test
    public void testExecute_UserInactive() throws Exception {
        when(credentialServiceMock.checkUserExists(USER_NAME)).thenReturn(true);
        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.INACTIVE)).thenReturn(true);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectWithCredentialError(SimbaMessageKey.LOGIN_FAILED);
    }
}
