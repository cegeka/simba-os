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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.service.CredentialService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.audit.AuditMessages.*;

public class CheckPasswordExpiredCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private static final String CLIENT_IP = "clientIP";

    private static final String USER_NAME = "bkkov";
    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @Mock private ChainContext contextMock;
    @Mock private CredentialService credentialServiceMock;
    @Mock private Audit auditMock;

    @Spy AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private CheckPasswordExpiredCommand command;

    @Before
    public void setUp() throws Exception {
    	when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        when(contextMock.getUserName()).thenReturn(USER_NAME);
    }

    @Test
    public void testExecute_notMustChangePassword_continue() throws Exception {
        when(credentialServiceMock.mustChangePasswordOnNextLogon(USER_NAME)).thenReturn(Boolean.FALSE);
        State result = command.execute(contextMock);
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(SUCCESS + CHECK_PASSWORD_EXPIRED, resultAuditLogEvent.getMessage());
        
        assertEquals(State.CONTINUE, result);
    }

    @Test
    public void testExecute_isChangePasswordRequest_continue() throws Exception {
        when(contextMock.isChangePasswordRequest()).thenReturn(Boolean.TRUE);
        State result = command.execute(contextMock);
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(SUCCESS + CHECK_PASSWORD_EXPIRED, resultAuditLogEvent.getMessage());
        
        assertEquals(State.CONTINUE, result);
    }

    @Test
    public void testExecute_mustChangePasswordAndRequestNotDirectlyToSimba_redirectToChangePasswordWithRequestURLAsTarget()
            throws Exception {
        when(credentialServiceMock.mustChangePasswordOnNextLogon(USER_NAME)).thenReturn(Boolean.TRUE);
        when(contextMock.getRequestURL()).thenReturn("requestURL");
        when(contextMock.getSimbaWebURL()).thenReturn("simbaWebURL");

        State state = command.execute(contextMock);

        assertEquals(State.FINISH, state);
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(MUST_CHANGE_PASSWORD, resultAuditLogEvent.getMessage());
		
        verify(contextMock).redirectToChangePasswordWithFilter();
    }

    @Test
    public void testExecute_mustChangePasswordAndRequestIsDirectlyToSimba_redirectToChangePasswordWithSuccesURLAsTarget()
            throws Exception {
        when(credentialServiceMock.mustChangePasswordOnNextLogon(USER_NAME)).thenReturn(Boolean.TRUE);
        when(credentialServiceMock.getSuccessURL(USER_NAME)).thenReturn("successURL");
        when(contextMock.getRequestURL()).thenReturn("requestURL");
        when(contextMock.getSimbaWebURL()).thenReturn("simbaWebURL");

        State state = command.execute(contextMock);

        assertEquals(State.FINISH, state);
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(MUST_CHANGE_PASSWORD, resultAuditLogEvent.getMessage());
        
        verify(contextMock).redirectToChangePasswordWithFilter();
    }
}
