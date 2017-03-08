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
package org.simbasecurity.common.request;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.simbasecurity.common.request.RequestConstants.SIMBA_SSO_TOKEN;

public final class RequestUtilTest {

    private static final String REMOTE_ADDRESS = "10.0.0.100";
    private static final String X_FORWARD_ADDRESS = "10.0.0.200";
    private static final String X_FORWARD_LIST = X_FORWARD_ADDRESS + " , 192.168.1.110";
    private static final String SERVLET_PATH = "/ServletPath";
    private static final String REQUEST_URL = "http://localhost:8080/zoo";
    private static final String NEW_TEMPORARY_TOKEN = "NewTemporaryToken";

    private HttpServletRequest requestMock;

    @Before
    public void setup() {
        requestMock = mock(HttpServletRequest.class);
        when(requestMock.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(requestMock.getParameterNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(requestMock.getRequestURL()).thenReturn(new StringBuffer());
        when(requestMock.getServletPath()).thenReturn(SERVLET_PATH);
    }

    @Test
    public void getClientIpAddressIsRemoteAddrFromRequest() {
        when(requestMock.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));

        assertThat(RequestUtil.retrieveClientIpAddress(requestMock)).isEqualTo(REMOTE_ADDRESS);
    }

    @Test
    public void getClientIpAddressIsXForwardedWhenPresent() {
        when(requestMock.getHeaderNames()).thenReturn(
                Collections.enumeration(Collections.singleton(RequestConstants.HEADER_X_FORWARDED_FOR)));
        when(requestMock.getHeader(RequestConstants.HEADER_X_FORWARDED_FOR)).thenReturn(X_FORWARD_ADDRESS);

        assertThat(RequestUtil.retrieveClientIpAddress(requestMock)).isEqualTo(X_FORWARD_ADDRESS);
    }

    @Test
    public void getClientIpAddressIsFirstInXForwardedList() {
        when(requestMock.getHeaderNames()).thenReturn(
                Collections.enumeration(Collections.singleton(RequestConstants.HEADER_X_FORWARDED_FOR)));
        when(requestMock.getHeader(RequestConstants.HEADER_X_FORWARDED_FOR)).thenReturn(X_FORWARD_LIST);

        assertThat(RequestUtil.retrieveClientIpAddress(requestMock)).isEqualTo(X_FORWARD_ADDRESS);
    }

    @Test
    public void addParametersToUrlAndFilterInternalParameters_filterSSOToken() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("par1", "val1");
        requestParams.put("par2", "val2");
        requestParams.put("par3", "val3");
        requestParams.put(SIMBA_SSO_TOKEN, NEW_TEMPORARY_TOKEN);
        requestParams.put("username", "bkbla");
        requestParams.put("password", "pazz");

        String result = RequestUtil.addParametersToUrlAndFilterInternalParameters(REQUEST_URL + "?foo=bar", requestParams);

        assertThat(result).startsWith(REQUEST_URL);
        assertThat(result).containsOnlyOnce("?");

        assertThat(result).containsOnlyOnce("foo=bar");
        assertThat(result).containsOnlyOnce("par1=val1");
        assertThat(result).containsOnlyOnce("par2=val2");
        assertThat(result).containsOnlyOnce("par3=val3");

        assertThat(result).doesNotContain(SIMBA_SSO_TOKEN);
        assertThat(result).doesNotContain("username");
        assertThat(result).doesNotContain("password");
    }

    @Test
    public void addParameterToUrl_doesntFilter() {
        String result = RequestUtil.addParameterToUrl(REQUEST_URL + "?foo=bar", SIMBA_SSO_TOKEN, NEW_TEMPORARY_TOKEN);
        assertThat(result).startsWith(REQUEST_URL);
        assertThat(result).containsOnlyOnce("?");
        assertThat(result).containsOnlyOnce("foo=bar");
        assertThat(result).containsOnlyOnce(SIMBA_SSO_TOKEN + "=" + NEW_TEMPORARY_TOKEN);
    }
}
