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

package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.*;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ValidateSAMLParametersCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Audit auditMock;
    @Mock private ChainContext chainContextMock;
    @Mock private SAMLService samlServiceMock;
    @Mock private SAMLResponseHandler samlResponseHandlerMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @InjectMocks private ValidateSAMLParametersCommand command;

    @Before
    public void setUp() throws Exception {
        when(samlServiceMock.getSAMLResponseHandler(null, null)).thenReturn(samlResponseHandlerMock);
    }

    @Test
    public void testExecute_Valid() throws Exception {
        when(samlResponseHandlerMock.isValid()).thenReturn(true);

        State state = command.execute(chainContextMock);

        verify(auditMock).log(captor.capture());
        AuditLogEvent resultAuditLogEvent = captor.getValue();

        assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
        assertEquals(AuditMessages.SUCCESS + AuditMessages.VALID_SAML_RESPONSE, resultAuditLogEvent.getMessage());

        assertEquals(State.CONTINUE, state);
    }

    @Test
    public void testExecute_Invalid() throws Exception {
        when(samlResponseHandlerMock.isValid()).thenReturn(false);

        State state = command.execute(chainContextMock);

        verify(chainContextMock).redirectToAccessDenied();

        verify(auditMock).log(captor.capture());
        AuditLogEvent resultAuditLogEvent = captor.getValue();

        assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
        assertEquals(AuditMessages.FAILURE + AuditMessages.INVALID_SAML_RESPONSE, resultAuditLogEvent.getMessage());

        assertEquals(State.FINISH, state);
    }
}