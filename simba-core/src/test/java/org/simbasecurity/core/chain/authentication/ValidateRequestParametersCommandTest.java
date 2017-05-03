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
package org.simbasecurity.core.chain.authentication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.audit.AuditMessages.EMPTY_PASSWORD;
import static org.simbasecurity.core.audit.AuditMessages.EMPTY_USERNAME;
import static org.simbasecurity.core.audit.AuditMessages.FAILURE;
import static org.simbasecurity.core.audit.AuditMessages.SUCCESS;
import static org.simbasecurity.core.audit.AuditMessages.VALID_REQUEST_PARAM;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventCategory;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.LoginMappingService;

@RunWith(MockitoJUnitRunner.class)
public class ValidateRequestParametersCommandTest {

	private static final String USER_NAME = "userName";
	private static final String PASSWORD = "Simba3D";
	private static final String CLIENT_IP = "10.0.0.1";

	@Mock private Audit auditMock;
	@Mock private ChainContext contextMock;
	@Mock private LoginMappingService loginMappingServiceMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private ValidateRequestParametersCommand command;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

	@Before
	public void setUp() throws Exception {
		when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
		when(contextMock.getUserName()).thenReturn(USER_NAME);
		when(contextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(PASSWORD);
	}

	@Test
	public void testExecute() throws Exception {
		assertEquals(State.CONTINUE, command.execute(contextMock));

		verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals( SUCCESS + VALID_REQUEST_PARAM, resultAuditLogEvent.getMessage());
	}

	@Test
	public void testExecute_EMPTY_USERNAME() throws Exception {
		when(contextMock.getUserName()).thenReturn(null);

		assertEquals(State.FINISH, command.execute(contextMock));
		verify(contextMock).redirectWithCredentialError(SimbaMessageKey.EMPTY_USERNAME);
		
		verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals( "not yet logged in", resultAuditLogEvent.getUsername());
		assertEquals( CLIENT_IP, FAILURE + EMPTY_USERNAME, resultAuditLogEvent.getMessage());
	}

	@Test
	public void testExecute_EMPTY_PASSWORD() throws Exception {
		when(contextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(null);

		assertEquals(State.FINISH, command.execute(contextMock));
		verify(contextMock).redirectWithCredentialError(SimbaMessageKey.EMPTY_PASSWORD);
		
		verify(auditMock).log(captor.capture());
		AuditLogEvent resultAuditLogEvent = captor.getValue();
		
		assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
		assertEquals(FAILURE + EMPTY_PASSWORD, resultAuditLogEvent.getMessage());
	}
}
