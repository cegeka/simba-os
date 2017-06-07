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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

public class CreateSessionCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private static final String NEW_TEMPORARY_TOKEN = "NewTemporaryToken";

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

    @Mock private Audit auditMock;
    @Mock private ChainContext contextMock;
    @Mock private SessionService sessionServiceMock;
    @Mock private CredentialService credentialServiceMock;
    @Mock private SSOTokenMappingService ssoTokenMappingServiceMock;

    @Mock private Session sessionMock;
    @Mock private SSOTokenMapping ssoTokenMappingMock;

    @Mock private AuditLogEventFactory eventFactory;

    @InjectMocks
    private CreateSessionCommand command;

    @Before
    public void setup() {
        when(contextMock.getUserName()).thenReturn(USER_NAME);
        when(contextMock.getClientIpAddress()).thenReturn(CLIENT_IP);
        when(contextMock.getHostServerName()).thenReturn(HOST_SERVER_NAME);
        when(contextMock.getUserAgent()).thenReturn(USER_AGENT);

        when(sessionMock.getSSOToken()).thenReturn(SSO_TOKEN);

        when(ssoTokenMappingMock.getToken()).thenReturn(NEW_TEMPORARY_TOKEN);
    }

    @Test
    public void redirectToTargetUrlFromMapping_byUsingJspLoginPage() throws Exception {
        when(contextMock.getRequestURL()).thenReturn(SIMBA_LOGIN_SERVLET);

        when(contextMock.isLoginUsingJSP()).thenReturn(Boolean.TRUE);

        LoginMapping loginMapping = LoginMappingEntity.create(TARGET_URL);
        when(contextMock.getLoginMapping()).thenReturn(loginMapping);

        when(ssoTokenMappingServiceMock.createMapping(SSO_TOKEN)).thenReturn(ssoTokenMappingMock);
        when(sessionServiceMock.createSession(USER_NAME, CLIENT_IP, HOST_SERVER_NAME, USER_AGENT, SIMBA_LOGIN_SERVLET)).thenReturn(sessionMock);

        assertEquals(State.FINISH, command.execute(contextMock));

        verify(contextMock, never()).activateAction(ActionType.MAKE_COOKIE);
        verify(contextMock, never()).setSSOTokenForActions(SSO_TOKEN);
        verify(contextMock).activateAction(ActionType.REDIRECT);
        verify(contextMock).setRedirectURL(TARGET_URL + "?" + SIMBA_SSO_TOKEN + "=" + NEW_TEMPORARY_TOKEN);
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
        verify(contextMock, never()).setSSOTokenForActions(SSO_TOKEN);
        verify(contextMock).activateAction(ActionType.REDIRECT);
        verify(contextMock).setRedirectURL(SUCCESS_URL + "?" + SIMBA_SSO_TOKEN + "=" + NEW_TEMPORARY_TOKEN);
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
        verify(contextMock).setRedirectURL(REQUEST_URL);
    }
}
