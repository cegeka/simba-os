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
package org.simbasecurity.core.jaas.callbackhandler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.chain.ChainContext;

@RunWith(MockitoJUnitRunner.class)
public class ChainContextCallbackHandlerTest {

    private static final String PASSWORD = "secret";
    private static final String USERNAME = "guest";

    @Mock private ChainContext contextMock;

    @InjectMocks
    private ChainContextCallbackHandler callbackHandler;

    @Test
    public void testHandleFormBasedLogin_testUsername() throws Exception {
        when(contextMock.getUserName()).thenReturn(USERNAME);

        Callback[] callbacks = new Callback[1];
        NameCallback nameCallback = new NameCallback(AuthenticationConstants.USERNAME);
        callbacks[0] = nameCallback;
        callbackHandler.handle(callbacks);

        assertEquals(USERNAME, nameCallback.getName());
    }

    @Test
    public void testHandleFormBasedLogin_testPassword() throws Exception {
        when(contextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(PASSWORD);

        Callback[] callbacks = new Callback[1];
        PasswordCallback passwordCallback = new PasswordCallback(AuthenticationConstants.PASSWORD, false);
        callbacks[0] = passwordCallback;
        callbackHandler.handle(callbacks);

        assertNotNull(passwordCallback.getPassword());
        assertEquals(PASSWORD, new String(passwordCallback.getPassword()));
    }

    @Test
    public void testHandleFormBasedLogin_passwordNull() throws Exception {
        when(contextMock.getRequestParameter(AuthenticationConstants.PASSWORD)).thenReturn(null);

        Callback[] callbacks = new Callback[1];
        PasswordCallback passwordCallback = new PasswordCallback(AuthenticationConstants.PASSWORD, false);
        callbacks[0] = passwordCallback;
        callbackHandler.handle(callbacks);

        assertEquals(null, passwordCallback.getPassword());
    }

    @Test(expected = UnsupportedCallbackException.class)
    public void gooitFoutIndienCallbackNietGekend() throws Exception {
        callbackHandler.handle(new Callback[]{new LanguageCallback()});
    }

}
