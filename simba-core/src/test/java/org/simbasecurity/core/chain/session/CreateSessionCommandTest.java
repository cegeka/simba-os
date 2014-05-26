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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.domain.SSOTokenMapping;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.SSOTokenMappingService;
import org.simbasecurity.core.service.SessionService;

@RunWith(MockitoJUnitRunner.class)
public class CreateSessionCommandTest {

	private static final String SIMBA_LOGIN_SERVLET = "http://localhost:8080/simba/http/simba-login";
	private static final String REQUEST_URL = "http://localhost:8080/zoo";
	private static final String USER_AGENT = "browser";
	private static final String SUCCESS_URL = "successURL";
	private static final String SIMBA_URL = "simbaURL";
	private static final String USER_NAME = "guest";
	private static final String CLIENT_IP = "10.0.0.100";
	private static final String HOST_SERVER_NAME = "192.168.1.1";
	private static final String TARGET_URL = "http://localhost:8080/zoo/main.jsp";

	private static final SSOToken SSO_TOKEN = new SSOToken();

	@Mock
	private Audit auditMock;
	@Mock
	private ChainContext contextMock;
	@Mock
	private SessionService sessionServiceMock;
	@Mock
	private CredentialService credentialServiceMock;
	@Mock
	private SSOTokenMappingService ssoTokenMappingServiceMock;

	@Mock
	private Session sessionMock;
	@Mock
	private SSOTokenMapping ssoTokenMappingMock;

	@Spy
	private AuditLogEventFactory auditLogEventFactory;

	@InjectMocks
	private CreateSessionCommand command;

	@Before
	public void setup() {
		when(contextMock.getSimbaWebURL()).thenReturn(SIMBA_URL);
		when(contextMock.getUserName()).thenReturn(USER_NAME);
		when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
		when(contextMock.getHostServerName()).thenReturn(HOST_SERVER_NAME);
		when(contextMock.getUserAgent()).thenReturn(USER_AGENT);

		when(sessionMock.getSSOToken()).thenReturn(SSO_TOKEN);
	}

	@Test
	public void redirectToTargetUrlFromMapping_byUsingJspLoginPage() throws Exception {
		when(contextMock.getRequestURL()).thenReturn(SIMBA_LOGIN_SERVLET);

		when(contextMock.isLoginUsingJSP()).thenReturn(Boolean.TRUE);

		LoginMapping loginMapping = new LoginMappingEntity(TARGET_URL);
		when(contextMock.getLoginMapping()).thenReturn(loginMapping);

		when(ssoTokenMappingServiceMock.createMapping(SSO_TOKEN)).thenReturn(ssoTokenMappingMock);
		when(sessionServiceMock.createSession(USER_NAME, CLIENT_IP, HOST_SERVER_NAME, USER_AGENT, SIMBA_LOGIN_SERVLET)).thenReturn(sessionMock);

		assertEquals(State.FINISH, command.execute(contextMock));

		verify(contextMock, never()).activateAction(ActionType.MAKE_COOKIE);
		verify(contextMock).setSSOTokenForActions(SSO_TOKEN);
		verify(contextMock).activateAction(ActionType.REDIRECT);
		verify(contextMock).setRedirectURL(TARGET_URL + "?" + SIMBA_SSO_TOKEN + "=" + SSO_TOKEN.getToken());
	}

	@Test
	public void redirectToSuccessURL() throws Exception {
		when(contextMock.getRequestURL()).thenReturn(SIMBA_LOGIN_SERVLET);
		when(contextMock.getLoginMapping()).thenReturn(null);

		when(contextMock.isLoginUsingJSP()).thenReturn(Boolean.TRUE);
		when(credentialServiceMock.getSuccessURL(USER_NAME)).thenReturn(SUCCESS_URL);

		when(ssoTokenMappingServiceMock.createMapping(SSO_TOKEN)).thenReturn(ssoTokenMappingMock);
		when(sessionServiceMock.createSession(USER_NAME, CLIENT_IP, HOST_SERVER_NAME, USER_AGENT, SIMBA_LOGIN_SERVLET)).thenReturn(sessionMock);

		assertEquals(State.FINISH, command.execute(contextMock));

		verify(contextMock, never()).activateAction(ActionType.MAKE_COOKIE);
		verify(contextMock).setSSOTokenForActions(SSO_TOKEN);
		verify(contextMock).activateAction(ActionType.REDIRECT);
		verify(contextMock).setRedirectURL(SUCCESS_URL + "?" + SIMBA_SSO_TOKEN + "=" + SSO_TOKEN.getToken());
	}

	@Test
	public void redirectToRequestURL_NoLoginMapping() throws Exception {
		when(contextMock.getRequestURL()).thenReturn(REQUEST_URL);
		when(contextMock.getLoginMapping()).thenReturn(null);
		when(contextMock.isLoginUsingJSP()).thenReturn(Boolean.FALSE);
		when(credentialServiceMock.getSuccessURL(USER_NAME)).thenReturn(SUCCESS_URL);
		when(ssoTokenMappingServiceMock.createMapping(SSO_TOKEN)).thenReturn(ssoTokenMappingMock);
		when(sessionServiceMock.createSession(USER_NAME, CLIENT_IP, HOST_SERVER_NAME, USER_AGENT, REQUEST_URL)).thenReturn(sessionMock);

		assertEquals(State.FINISH, command.execute(contextMock));

		verify(contextMock).setSSOTokenForActions(SSO_TOKEN);
		verify(contextMock).activateAction(ActionType.MAKE_COOKIE);
		verify(contextMock).activateAction(ActionType.REDIRECT);
		verify(contextMock).setRedirectURL(REQUEST_URL + "?" + SIMBA_SSO_TOKEN + "=" + SSO_TOKEN.getToken());
	}

	@Test
	public void ssoMappingTokenDoesNotRemoveOtherRequestParams() throws Exception {

		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("par1", "val1");
		requestParams.put("par2", "val2");
		requestParams.put("par3", "val3");
		requestParams.put("username", "bkbla");
		requestParams.put("password", "pazz");

		String result = command.addMappingTokenToUrl(REQUEST_URL + "?foo=bar", ssoTokenMappingMock, requestParams);
		assertTrue(result.startsWith(REQUEST_URL));
		assertTrue(result.indexOf('?') == result.lastIndexOf('?')); // only 1
		assertTrue(result.contains("foo=bar"));
		assertTrue(result.contains("par1=val1"));
		assertTrue(result.contains("par2=val2"));
		assertTrue(result.contains("par3=val3"));
		assertTrue(result.contains(SIMBA_SSO_TOKEN + "=" + SSO_TOKEN.getToken()));

		assertFalse(result.contains("username=bkbla"));
		assertFalse(result.contains("password=pazz"));
	}

}
