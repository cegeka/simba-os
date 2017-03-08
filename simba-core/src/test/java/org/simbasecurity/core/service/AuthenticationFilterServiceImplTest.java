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
package org.simbasecurity.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.common.request.RequestConstants;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.ChainImpl;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.test.LocatorTestCase;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationFilterServiceImplTest extends LocatorTestCase {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private SessionService sessionServiceMock;
    @Mock private LoginMappingService loginMappingService;
    @Mock private SSOTokenMappingService ssoTokenMappingService;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private AuthenticationFilterServiceImpl serviceImpl;

    @Test
    public void testProcessRequest() throws Exception {
        Session sessionMock = mock(Session.class);
        when(sessionServiceMock.getSession(any(SSOToken.class))).thenReturn(sessionMock);

        ChainImpl authenticationChainMock = implantMockLocatingByNameOnly(ChainImpl.class, "authenticationChain");

        serviceImpl.processRequest(new RequestData(null, null, null, null, null, null, false, false, false, false, false,
                                                  null, null, "loginToken"), "authenticationChain");
        verify(authenticationChainMock).execute(any(ChainContext.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessRequest_InvalidChainCommand_throwsIllegalArgument() throws Exception {
        serviceImpl.processRequest(new RequestData(null, null, null, null, null, null, false, false, false, false, false,
                                                  null, null, "loginToken"), "blabla");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessRequest_InvalidChainCommandNull_throwsIllegalArgument() throws Exception {
        serviceImpl.processRequest(new RequestData(null, null, null, null, null, null, false, false, false, false, false,
                                                  null, null, "loginToken"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessRequest_InvalidRequestData_throwsIllegalArgument() throws Exception {
        serviceImpl.processRequest(null, "blabla");
    }

    @Test
    public void testGetCurrentSession_NoTokenMappingProvided() {
        SSOToken ssoToken = mock(SSOToken.class);
        RequestData requestData = new RequestData(null, null, null, null, ssoToken, null, false, false, false, false,
                                                 false, null, null, null);

        serviceImpl.getCurrentSession(requestData);

        verify(sessionServiceMock).getSession(same(ssoToken));
    }

    @Test
    public void testGetCurrentSession_TokenMappingProvided() {
        SSOToken ssoToken = mock(SSOToken.class);
        String tokenKey = UUID.randomUUID().toString();
        RequestData requestData = new RequestData(Collections.<String, String>singletonMap(RequestConstants.SIMBA_SSO_TOKEN, tokenKey),
                                                 null, null, null, null, null, false, false, true, false, false, null, null, null);

        when(ssoTokenMappingService.getSSOToken(tokenKey)).thenReturn(ssoToken);

        serviceImpl.getCurrentSession(requestData);

        verify(sessionServiceMock).getSession(same(ssoToken));
        verify(ssoTokenMappingService).destroyMapping(tokenKey);
    }
}
