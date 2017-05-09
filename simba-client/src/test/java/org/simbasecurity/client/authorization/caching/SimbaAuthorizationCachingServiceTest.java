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
package org.simbasecurity.client.authorization.caching;

import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.api.service.thrift.PolicyDecision;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SimbaAuthorizationCachingServiceTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final long EXPIRED_TIMESTAMP = System.currentTimeMillis() - 1000;
    private static final long VALID_TIMESTAMP = Long.MAX_VALUE;

    private static final String NOT_RELEVANT = "NOT_RELEVANT";

    @Mock
    private AuthorizationService.Iface authorizationServiceMock;

    private AuthorizationServiceClient cachingService;

    @Before
    public void setUp() throws Exception {
        cachingService = new AuthorizationServiceClient() {
            @Override
            protected AuthorizationService.Iface getAuthorizationServiceClient() throws TTransportException {
                return authorizationServiceMock;
            }
        };
    }

    @Test
    public void shouldCallAuthorizationServiceWhenResourceRuleNotCached() throws Exception {
        cachingService.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        verify(authorizationServiceMock).isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
    }

    @Test
    public void shouldCallAuthorizationServiceWhenResourceRuleCachedButExpired() throws Exception {
        when(authorizationServiceMock.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT))
                .thenReturn(new PolicyDecision(true, EXPIRED_TIMESTAMP))
                .thenReturn(new PolicyDecision(false, VALID_TIMESTAMP));

        cachingService.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT); // Call once to fill cache

        PolicyDecision decision = cachingService.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        assertFalse(decision.isAllowed());
        verify(authorizationServiceMock, times(2)).isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
    }

    @Test
    public void shouldNotCallAuthorizationServiceWhenResourceRuleCached() throws Exception {
        when(authorizationServiceMock.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT))
                .thenReturn(new PolicyDecision(true, VALID_TIMESTAMP));

        cachingService.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT); // Call once to fill cache

        PolicyDecision decision = cachingService.isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        assertTrue(decision.isAllowed());
        verify(authorizationServiceMock).isResourceRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
        verifyNoMoreInteractions(authorizationServiceMock);
    }

    @Test
    public void shouldCallAuthorizationServiceWhenURLRuleNotCached() throws Exception {
        cachingService.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        verify(authorizationServiceMock).isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
    }

    @Test
    public void shouldCallAuthorizationServiceWhenURLRuleCachedButExpired() throws Exception {
        when(authorizationServiceMock.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT))
                .thenReturn(new PolicyDecision(true, EXPIRED_TIMESTAMP))
                .thenReturn(new PolicyDecision(false, VALID_TIMESTAMP));

        cachingService.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT); // Call once to fill cache

        PolicyDecision decision = cachingService.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        assertFalse(decision.isAllowed());
        verify(authorizationServiceMock, times(2)).isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
    }

    @Test
    public void shouldNotCallAuthorizationServiceWhenURLRuleCached() throws Exception {
        when(authorizationServiceMock.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT))
                .thenReturn(new PolicyDecision(true, VALID_TIMESTAMP));

        cachingService.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT); // Call once to fill cache

        PolicyDecision decision = cachingService.isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);

        assertTrue(decision.isAllowed());
        verify(authorizationServiceMock).isURLRuleAllowed(NOT_RELEVANT, NOT_RELEVANT, NOT_RELEVANT);
        verifyNoMoreInteractions(authorizationServiceMock);
    }

}
