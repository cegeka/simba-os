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
import static org.simbasecurity.core.audit.AuditMessages.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Status;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.CredentialService;

@RunWith(MockitoJUnitRunner.class)
public class CheckAccountBlockedCommandTest {

    private static final String USER_NAME = "userName";
    private static final String CLIENT_IP = "clientIp";

    @Mock private CredentialService credentialServiceMock;
    @Mock private Audit auditMock;
    @Mock private ChainContext contextMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @InjectMocks
    private CheckAccountBlockedCommand command;

    @Test
    public void testExecute_AccountBlocked() throws Exception {
        when(contextMock.getUserName()).thenReturn(USER_NAME);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.BLOCKED)).thenReturn(true);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(FAILURE + DENIED_ACCESS_TO_BLOCKED_ACCOUNT, resultAuditLogEvent.getMessage());
        verify(contextMock).redirectWithCredentialError(SimbaMessageKey.ACCOUNT_BLOCKED);
    }

    @Test
    public void testExecute_AccountNotBlocked() throws Exception {
        when(contextMock.getUserName()).thenReturn(USER_NAME);
        when(credentialServiceMock.checkUserStatus(USER_NAME, Status.BLOCKED)).thenReturn(false);
        assertEquals(State.CONTINUE, command.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(SUCCESS + CHECK_ACCOUNT_BLOCKED, resultAuditLogEvent.getMessage());
    }

}
