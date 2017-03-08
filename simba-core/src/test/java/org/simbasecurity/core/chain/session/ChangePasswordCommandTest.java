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

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.audit.*;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.CredentialService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORD_INVALID_LENGTH;

public class ChangePasswordCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private static final String USERNAME = "bkkov";
    private static final String OLD_PASSWORD = "password";
    private static final String NEW_PASSWORD = "password1";
    private static final String IP_ADDRESS = "ipaddress";

    @Mock private Audit auditMock;
    @Mock private ChainContext chainContextMock;
    @Mock private CredentialService credentialServiceMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @InjectMocks
    private ChangePasswordCommand command;

    @Test
    public void testPasswordChangeOk() throws Exception {
        when(chainContextMock.isChangePasswordRequest()).thenReturn(Boolean.TRUE);
        when(chainContextMock.getUserName()).thenReturn(USERNAME);
        when(chainContextMock.getClientIpAddress()).thenReturn(IP_ADDRESS);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(OLD_PASSWORD);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.NEW_PASSWORD)).thenReturn(NEW_PASSWORD);

        State state = command.execute(chainContextMock);

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.SESSION, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.SUCCESS + AuditMessages.PASSWORD_CHANGED, resultAuditLogEvent.getMessage());
		
        assertEquals(State.CONTINUE, state);
    }

    @Test
    public void testPasswordChangeDuringSessionRedirectToPasswordChanged() throws Exception {
        Session sessionMock = mock(Session.class);
        when(chainContextMock.getCurrentSession()).thenReturn(sessionMock);
        when(chainContextMock.isChangePasswordRequest()).thenReturn(Boolean.TRUE);
        when(chainContextMock.getUserName()).thenReturn(USERNAME);
        when(chainContextMock.getClientIpAddress()).thenReturn(IP_ADDRESS);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(OLD_PASSWORD);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.NEW_PASSWORD)).thenReturn(NEW_PASSWORD);

        State state = command.execute(chainContextMock);

        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.SESSION, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.SUCCESS + AuditMessages.PASSWORD_CHANGED, resultAuditLogEvent.getMessage());
        
        verify(chainContextMock).redirectToPasswordChanged();
        assertEquals(State.FINISH, state);
    }

    @Test
    public void testPasswordReset_passwordNotValid() throws Exception {
        String simbaURL = "simbaURL";
        String requestURL = "requestURL";
        when(chainContextMock.getSimbaWebURL()).thenReturn(simbaURL);
        when(chainContextMock.getRequestURL()).thenReturn(requestURL);
        when(chainContextMock.getClientIpAddress()).thenReturn(IP_ADDRESS);
        when(chainContextMock.getUserName()).thenReturn(USERNAME);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.NEW_PASSWORD)).thenReturn(NEW_PASSWORD);
        when(chainContextMock.getRequestParameter(AuthenticationConstants.NEW_PASSWORD_CONFIRMATION)).thenReturn(
                NEW_PASSWORD);
        doThrow(new SimbaException(PASSWORD_INVALID_LENGTH)).when(credentialServiceMock).changePassword(USERNAME,
                NEW_PASSWORD, NEW_PASSWORD);
        when(chainContextMock.isChangePasswordRequest()).thenReturn(Boolean.TRUE);

        State state = command.execute(chainContextMock);

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.SESSION, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.FAILURE + AuditMessages.PASSWORD_NOT_VALID, resultAuditLogEvent.getMessage());
        
        verify(chainContextMock).redirectWithCredentialError(PASSWORD_INVALID_LENGTH);
        assertEquals(State.FINISH, state);
    }

    @Test
    public void testPasswordReset_notPasswordResetRequest() throws Exception {
        when(chainContextMock.isChangePasswordRequest()).thenReturn(Boolean.FALSE);

        State state = command.execute(chainContextMock);
        assertEquals(State.CONTINUE, state);
    }
}
