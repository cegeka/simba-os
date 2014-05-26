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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RequestUtilTest {

    private static final String REMOTE_ADDRESS = "10.0.0.100";
    private static final String X_FORWARD_ADDRESS = "10.0.0.200";
    private static final String X_FORWARD_LIST = X_FORWARD_ADDRESS + " , 192.168.1.110";
    private static final String SERVLET_PATH = "/ServletPath";

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
    public void testGetClientIpAddressIsRemoteAddrFromRequest() {
        when(requestMock.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));

        assertEquals(REMOTE_ADDRESS, RequestUtil.retrieveClientIpAddress(requestMock));
    }

    @Test
    public void testGetClientIpAddressIsXForwardedWhenPresent() {
        when(requestMock.getHeaderNames()).thenReturn(
                Collections.enumeration(Collections.singleton(RequestConstants.HEADER_X_FORWARDED_FOR)));
        when(requestMock.getHeader(RequestConstants.HEADER_X_FORWARDED_FOR)).thenReturn(X_FORWARD_ADDRESS);

        assertEquals(X_FORWARD_ADDRESS, RequestUtil.retrieveClientIpAddress(requestMock));
    }

    @Test
    public void testGetClientIpAddressIsFirstInXForwardedList() {
        when(requestMock.getHeaderNames()).thenReturn(
                Collections.enumeration(Collections.singleton(RequestConstants.HEADER_X_FORWARDED_FOR)));
        when(requestMock.getHeader(RequestConstants.HEADER_X_FORWARDED_FOR)).thenReturn(X_FORWARD_LIST);

        assertEquals(X_FORWARD_ADDRESS, RequestUtil.retrieveClientIpAddress(requestMock));
    }
}
