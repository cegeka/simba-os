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
package org.simbasecurity.common.filter.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.common.request.RequestConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public final class MakeCookieActionTest {

    private static final String REQUEST_VALUE = "value added to the request";
    private static final String RESPONSE_HEADER = "RESPONSE_HEADER";
    private static final String REQUEST_HEADER = "REQUEST_HEADER";
    private static final SSOToken SSO_TOKEN = new SSOToken("testToken");

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader(REQUEST_HEADER)).thenReturn(REQUEST_VALUE);
        response = mock(HttpServletResponse.class);
        when(response.containsHeader(RESPONSE_HEADER)).thenReturn(true);
    }

    @Test
    public void testExecute() throws Exception {
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<>(), new HashMap<>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.MAKE_COOKIE);
        actionDescriptor.setSsoToken(SSO_TOKEN);

        MakeCookieAction action = new MakeCookieAction(actionDescriptor);
        action.setRequest(request);
        action.setResponse(response);
        action.execute();

        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());

        Cookie cookie = captor.getValue();
        assertThat(cookie.getName()).isEqualTo(RequestConstants.SIMBA_SSO_TOKEN);
        assertThat(cookie.getValue()).isEqualTo(SSO_TOKEN.getToken());
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

    @Test
    public void testExecute_WithSecureCookies() throws Exception {
        MakeCookieAction.setSecureCookiesEnabled(true);
        ActionDescriptor actionDescriptor = new ActionDescriptor(new HashSet<>(), new HashMap<>(), null, null, null, null);
        actionDescriptor.getActionTypes().add(ActionType.MAKE_COOKIE);
        actionDescriptor.setSsoToken(SSO_TOKEN);

        MakeCookieAction action = new MakeCookieAction(actionDescriptor);
        action.setRequest(request);
        action.setResponse(response);
        action.execute();


        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(captor.capture());

        Cookie cookie = captor.getValue();
        assertThat(cookie.getName()).isEqualTo(RequestConstants.SIMBA_SSO_TOKEN);
        assertThat(cookie.getValue()).isEqualTo(SSO_TOKEN.getToken());
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }

}
