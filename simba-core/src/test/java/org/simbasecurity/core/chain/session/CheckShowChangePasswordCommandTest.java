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
import static org.simbasecurity.core.audit.AuditMessages.*;
import static org.simbasecurity.core.chain.Command.State.*;

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

@RunWith(MockitoJUnitRunner.class)
public class CheckShowChangePasswordCommandTest {

    @Mock private ChainContext chainContextMock;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;
    
    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @InjectMocks
    private CheckShowChangePasswordCommand command;

    @Test
    public void redirectToChangePasswordPageWhenIsShowChangePassword() throws Exception {
        when(chainContextMock.isShowChangePasswordRequest()).thenReturn(true);

        State state = command.execute(chainContextMock);

        verify(chainContextMock).redirectToChangePasswordDirect();
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();

		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(REDIRECT_TO_CHANGE_PASSWORD, resultAuditLogEvent.getMessage());
		
        assertEquals(FINISH, state);
    }

    @Test
    public void continueOtherwise() throws Exception {
        when(chainContextMock.isShowChangePasswordRequest()).thenReturn(false);

        command.execute(chainContextMock);

        verify(chainContextMock).isShowChangePasswordRequest();
        verify(chainContextMock).getUserName();
        verify(chainContextMock).getClientIpAddress();
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(SUCCESS + CHECK_SHOW_PASSWORD, resultAuditLogEvent.getMessage());
    }
}
