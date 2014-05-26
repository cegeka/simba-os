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

import static java.lang.Boolean.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.audit.AuditMessages.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.audit.AuditMessages;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.service.ExcludedResourceService;

@RunWith(MockitoJUnitRunner.class)
public class ExcludeResourceCommandTest {
    private static final String DUMMY_URL = "dummy_url";
    private static final String USERNAME = "username";
    private static final String CLIENT_IP = "10.0.0.100";

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);;

    @Mock private ExcludedResourceService mockExcludedResourceService;
    @Mock private Audit auditMock;
    @Mock private ChainContext contextMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private ExcludeResourceCommand command;

    @Before
    public void setUp() throws Exception {
        when(contextMock.getRequestURL()).thenReturn(DUMMY_URL);
        when(contextMock.getUserName()).thenReturn(USERNAME);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
    }

    @Test
    public void testExecute_resourceNotExcluded_continues() throws Exception {
        when(mockExcludedResourceService.isResourceExcluded(DUMMY_URL)).thenReturn(FALSE);
        assertEquals(State.CONTINUE, command.execute(contextMock));
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(AuditMessages.NO_EXCLUDED_RESOURCE + DUMMY_URL, resultAuditLogEvent.getMessage());
    }

    @Test
    public void testExecute_resourceExcluded_finishDoFilterSetPrincipal() throws Exception {
        when(mockExcludedResourceService.isResourceExcluded(DUMMY_URL)).thenReturn(TRUE);
        assertEquals(State.FINISH, command.execute(contextMock));
        verify(contextMock).activateAction(ActionType.DO_FILTER_AND_SET_PRINCIPAL);
        
        verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		
		assertEquals(SUCCESS + "Resource excluded [" + DUMMY_URL + "]", resultAuditLogEvent.getMessage());
    }

}
