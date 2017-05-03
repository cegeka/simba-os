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
package org.simbasecurity.core.chain.authorization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;

@RunWith(MockitoJUnitRunner.class)
public class URLRuleCheckCommandTest {

    private static final String USERNAME = "username";
    private static final String REQUEST_URL = "request url";
    private static final String REQUEST_METHOD = "get";
    private static final String CLIENT_IP = "10.0.0.100";
    private static final SSOToken SSO_TOKEN = new SSOToken();

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);
    @Mock private ChainContext contextMock;
    @Mock private AuthorizationService.Iface authorizationServiceMock;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private URLRuleCheckCommand command;

    @Before
    public void setUp() throws Exception {
    	when(contextMock.getUserName()).thenReturn(USERNAME);
        when(contextMock.getRequestMethod()).thenReturn(REQUEST_METHOD);
        when(contextMock.getRequestURL()).thenReturn(REQUEST_URL);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        when(contextMock.getRequestSSOToken()).thenReturn(SSO_TOKEN);
    }

    @Test
    public void continueWhenAccessIsAllowed() throws Exception {
        when(authorizationServiceMock.isURLRuleAllowed(USERNAME, REQUEST_URL, REQUEST_METHOD)).thenReturn(
                new PolicyDecision(true, Long.MAX_VALUE));

        assertEquals(State.CONTINUE, command.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHOR, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.SUCCESS + AuditMessages.CHECK_URL_RULE, resultAuditLogEvent.getMessage());
        verifyZeroInteractions(auditMock);
        verify(contextMock, never()).redirectToAccessDenied();
    }

    @Test
    public void redirectWhenAccessIsDisallowed() throws Exception {
        when(authorizationServiceMock.isURLRuleAllowed(USERNAME, REQUEST_URL, REQUEST_METHOD)).thenReturn(
                new PolicyDecision(false, Long.MAX_VALUE));

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHOR, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.FAILURE + AuditMessages.ACCESS_DENIED + REQUEST_URL, resultAuditLogEvent.getMessage());
        verify(contextMock).redirectToAccessDenied();
    }

}
