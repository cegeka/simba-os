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
package org.simbasecurity.core.chain;

import com.google.common.collect.Maps;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.service.LoginMappingService;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.simbasecurity.api.service.thrift.ActionType.ADD_PARAMETER_TO_TARGET;
import static org.simbasecurity.api.service.thrift.ActionType.REDIRECT;
import static org.simbasecurity.common.constants.AuthenticationConstants.*;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;
import static org.simbasecurity.core.exception.SimbaMessageKey.LOGIN_FAILED;

public class ChainContextImplTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private static final String SIMBA_LOGIN_PAGE_URL = "login_url";
    private static final String SIMBA_CHANGEPASSWORD_PAGE_URL = "/jsp/changepassword.jsp";
    private static final String SIMBA_PASSWORD_CHANGED_URL = "/jsp/passwordchanged.jsp";
    private static final String SIMBA_PASSWORD_RESET_URL = "/jsp/password-reset.jsp";

    private static final String SIMBA_PASSWORD_INVALID_URL = "/jsp/password-invalid-token.jsp";

    private static final String SIMBA_WEB_URL = "http://simba_web_url/simbaContextRoot/";
    private static final String REQUEST_URL = "http://localhost:8080/simba/http/simba-change-pwd";

    private static final String URL_APPLICATION = "http://localhost:8080/simba-zoo";

    private static final String USERNAME = "username";

    @Mock private RequestData requestDataMock;
    @Mock private ConfigurationServiceImpl configurationServiceMock;
    @Mock private LoginMappingService loginMappingServiceMock;

    private ChainContextImpl chainContextImpl;

    @Before
    public void setUp() {
        chainContextImpl = new ChainContextImpl(requestDataMock, null, configurationServiceMock, loginMappingServiceMock);
        when(requestDataMock.getSimbaWebURL()).thenReturn(SIMBA_WEB_URL);
        when(configurationServiceMock.getValue(LOGIN_URL)).thenReturn(SIMBA_LOGIN_PAGE_URL);
        when(configurationServiceMock.getValue(CHANGE_PASSWORD_URL)).thenReturn(SIMBA_CHANGEPASSWORD_PAGE_URL);
    }

    @Test
    public void redirectToChangePasswordWithFilter_alreadyHasToken() {
        when(requestDataMock.getRequestURL()).thenReturn(REQUEST_URL);

        String loginToken = "uniqueToken1245698";
        when(requestDataMock.getLoginToken()).thenReturn(loginToken);
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, USERNAME));

        chainContextImpl.redirectToChangePasswordWithFilter();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_CHANGEPASSWORD_PAGE_URL, actionDescriptor.getRedirectURL());

        Map<String, String> parameterMap = actionDescriptor.getParameterMap();
        assertEquals(1, parameterMap.size());
        assertTrue(parameterMap.containsKey(USERNAME));
    }

    @Test
    public void redirectToChangePasswordWithFilter_noToken() {
        when(requestDataMock.getRequestURL()).thenReturn(URL_APPLICATION);

        when(requestDataMock.getLoginToken()).thenReturn(null);
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, USERNAME));

        LoginMapping loginMapping = LoginMappingEntity.create(URL_APPLICATION);
        when(loginMappingServiceMock.createMapping(URL_APPLICATION)).thenReturn(loginMapping);

        chainContextImpl.redirectToChangePasswordWithFilter();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_CHANGEPASSWORD_PAGE_URL, actionDescriptor.getRedirectURL());

        Map<String, String> parameterMap = actionDescriptor.getParameterMap();
        assertEquals(2, parameterMap.size());
        assertTrue(parameterMap.containsKey(USERNAME));
        assertTrue(parameterMap.containsKey(LOGIN_TOKEN));

        Mockito.verify(loginMappingServiceMock).createMapping(URL_APPLICATION);
    }

    @Test
    public void redirectToChangePasswordDirect_userIsInTheApplication_ClicksChangePwd() {
        when(requestDataMock.getRequestURL()).thenReturn(URL_APPLICATION);
        SSOToken ssoToken = new SSOToken();
        when(requestDataMock.getSsoToken()).thenReturn(ssoToken);

        LoginMapping loginMapping = LoginMappingEntity.create(URL_APPLICATION);
        when(loginMappingServiceMock.createMapping(URL_APPLICATION)).thenReturn(loginMapping);

        chainContextImpl.redirectToChangePasswordDirect();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_CHANGEPASSWORD_PAGE_URL, actionDescriptor.getRedirectURL());

        Map<String, String> parameterMap = actionDescriptor.getParameterMap();
        assertEquals(3, parameterMap.size());
        assertTrue(parameterMap.containsKey(USERNAME));
        assertTrue(parameterMap.containsKey(SIMBA_SSO_TOKEN));
        assertTrue(parameterMap.containsKey(LOGIN_TOKEN));

        Mockito.verify(loginMappingServiceMock).createMapping(URL_APPLICATION);
    }

    @Test
    public void redirectToPasswordChanged() {
        when(configurationServiceMock.getValue(PASSWORD_CHANGED_URL)).thenReturn(SIMBA_PASSWORD_CHANGED_URL);

        chainContextImpl.redirectToPasswordChanged();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_PASSWORD_CHANGED_URL, actionDescriptor.getRedirectURL());
    }

    @Test
    public void redirectToPasswordReset(){
        when(configurationServiceMock.getValue(PASSWORD_RESET_URL)).thenReturn(SIMBA_PASSWORD_RESET_URL);

        chainContextImpl.redirectToPasswordReset();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_PASSWORD_RESET_URL, actionDescriptor.getRedirectURL());
    }

    @Test
    public void redirectToLogin() {
        when(requestDataMock.getRequestURL()).thenReturn(URL_APPLICATION);
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, USERNAME));

        LoginMapping loginMapping = LoginMappingEntity.create(URL_APPLICATION);
        when(loginMappingServiceMock.createMapping(URL_APPLICATION)).thenReturn(loginMapping);

        chainContextImpl.redirectToLogin();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_LOGIN_PAGE_URL, actionDescriptor.getRedirectURL());

        Map<String, String> parameterMap = actionDescriptor.getParameterMap();
        assertEquals(1, parameterMap.size());
        assertTrue(parameterMap.containsKey(LOGIN_TOKEN));

        Mockito.verify(loginMappingServiceMock).createMapping(URL_APPLICATION);
    }

    @Test
    public void redirectWithCredentialError() {
        when(requestDataMock.getRequestURL()).thenReturn(URL_APPLICATION);
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, USERNAME));
        when(requestDataMock.isChangePasswordRequest()).thenReturn(Boolean.FALSE);
        when(requestDataMock.isLoginRequest()).thenReturn(Boolean.TRUE);

        when(configurationServiceMock.getValue(LOGIN_URL)).thenReturn(SIMBA_LOGIN_PAGE_URL);

        LoginMapping loginMapping = LoginMappingEntity.create(URL_APPLICATION);
        when(loginMappingServiceMock.createMapping(URL_APPLICATION)).thenReturn(loginMapping);

        chainContextImpl.redirectWithCredentialError(LOGIN_FAILED);
        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertTrue(actionTypes.contains(REDIRECT));
        assertEquals(SIMBA_WEB_URL + SIMBA_LOGIN_PAGE_URL, actionDescriptor.getRedirectURL());

        Map<String, String> parameterMap = actionDescriptor.getParameterMap();
        assertEquals(3, parameterMap.size());
        assertTrue(parameterMap.containsKey(USERNAME));
        assertTrue(parameterMap.containsKey(ERROR_MESSAGE));
        assertTrue(parameterMap.containsKey(LOGIN_TOKEN));
        Mockito.verify(loginMappingServiceMock).createMapping(URL_APPLICATION);
    }

    @Test
    public void getEmailWillReturnEmailIfPresentInRequestParameters(){
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(EMAIL, "someEmail"));

        Optional<String> email = chainContextImpl.getEmail();

        assertThat(email).contains("someEmail");
    }

    @Test
    public void getEmailWillReturnEmptyIfNotPresentInRequestParameters(){
        when(requestDataMock.getRequestParameters()).thenReturn(newHashMap());
        assertThat(chainContextImpl.getEmail()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, "someEmail"));
        assertThat(chainContextImpl.getEmail()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(EMAIL, null));
        assertThat(chainContextImpl.getEmail()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(EMAIL, ""));
        assertThat(chainContextImpl.getEmail()).isEmpty();
    }

    @Test
    public void getTokenWillReturnTokenIfPresentInRequestParameters() throws Exception {
        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(TOKEN, "someToken"));

        Optional<String> token = chainContextImpl.getToken();

        assertThat(token).contains("someToken");
    }

    @Test
    public void getTokenWillReturnEmptyIfNotPresentInRequestParameters(){
        when(requestDataMock.getRequestParameters()).thenReturn(newHashMap());
        assertThat(chainContextImpl.getToken()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(USERNAME, "someToken"));
        assertThat(chainContextImpl.getToken()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(TOKEN, null));
        assertThat(chainContextImpl.getToken()).isEmpty();

        when(requestDataMock.getRequestParameters()).thenReturn(Collections.singletonMap(TOKEN, ""));
        assertThat(chainContextImpl.getToken()).isEmpty();
    }

    @Test
    public void redirectToWrongToken(){
        when(configurationServiceMock.getValue(PASSWORD_INVALID_TOKEN_URL)).thenReturn(SIMBA_PASSWORD_INVALID_URL);

        chainContextImpl.redirectToWrongToken();

        ActionDescriptor actionDescriptor = chainContextImpl.getActionDescriptor();
        Set<ActionType> actionTypes = actionDescriptor.getActionTypes();
        assertEquals(2, actionTypes.size());
        assertTrue(actionTypes.contains(REDIRECT));
        assertTrue(actionTypes.contains(ADD_PARAMETER_TO_TARGET));
        assertEquals(SIMBA_WEB_URL + SIMBA_PASSWORD_INVALID_URL, actionDescriptor.getRedirectURL());
    }
}
