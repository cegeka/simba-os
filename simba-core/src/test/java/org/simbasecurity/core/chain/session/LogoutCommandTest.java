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
package org.simbasecurity.core.chain.session;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.SessionService;

@RunWith(MockitoJUnitRunner.class)
public class LogoutCommandTest {

    private static final String CLIENT_IP = "127.0.0.1";
    private static final String USERNAME = "user1";

    @Mock private SessionService sessionService;
    @Mock private ChainContext chainContext;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private LogoutCommand logoutCommand;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);
    
    @Test
    public void onLogoutRequestRemoveSessionAndRedirectAndDeleteCookie() throws Exception {
        
    	SSOToken sSOToken = new SSOToken("token");
		when(chainContext.getUserName()).thenReturn(USERNAME);
		when(chainContext.getClientIpAddress()).thenReturn(CLIENT_IP);
		when(chainContext.isLogoutRequest()).thenReturn(true);
		
		Session sessionMock = mock(Session.class);
		when(chainContext.getCurrentSession()).thenReturn(sessionMock);
		when(chainContext.getRequestSSOToken()).thenReturn(sSOToken);
		
    	State state = logoutCommand.execute(chainContext);

        assertEquals(State.FINISH, state);
        verify(sessionService).removeSession(isA(Session.class));
        verify(chainContext).activateAction(ActionType.DELETE_COOKIE);
        verify(chainContext).redirectToLogout();
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.SESSION, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.SUCCESS + AuditMessages.LOGGED_OUT + ": SSOToken="+sSOToken, resultAuditLogEvent.getMessage());
		
    }

    @Test
    public void continuesIfNotLogoutRequest() throws Exception {
        when(chainContext.isLogoutRequest()).thenReturn(false);

        State state = logoutCommand.execute(chainContext);

        assertEquals(State.CONTINUE, state);
    }
}
