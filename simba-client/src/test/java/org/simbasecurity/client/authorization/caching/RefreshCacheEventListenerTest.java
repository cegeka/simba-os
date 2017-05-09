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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.common.event.SimbaEvent;
import org.simbasecurity.common.event.SimbaEventType;

import javax.jms.ObjectMessage;

import static org.mockito.Mockito.*;

public class RefreshCacheEventListenerTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String USERNAME = "test";

    @Mock
    private AuthorizationServiceClient service;

    private RefreshCacheEventListener listener;

    @Before
    public void setUp() {
        listener = new RefreshCacheEventListener(service);
    }

    @Test
    public void onMessage_AuthorizationChanged() throws Exception {
        SimbaEvent event = new SimbaEvent(SimbaEventType.AUTHORIZATION_CHANGED, null);

        ObjectMessage message = mock(ObjectMessage.class);
        when(message.getObject()).thenReturn(event);

        listener.onMessage(message);

        verify(service).invalidate();
    }

    @Test
    public void onMessage_UserAuthorizationChanged() throws Exception {
        SimbaEvent event = new SimbaEvent(SimbaEventType.USER_AUTHORIZATION_CHANGED, USERNAME);

        ObjectMessage message = mock(ObjectMessage.class);
        when(message.getObject()).thenReturn(event);

        listener.onMessage(message);

        verify(service).invalidate(USERNAME);
    }
}
