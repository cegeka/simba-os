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

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.junit.Before;
import org.junit.Test;

public class WsPlainTextCallbackHandlerTest {

    private String userName = "bkkov";
    private String password = "haha";
    private WsPlainTextCallbackHandler handler;


    @Before
    public void setUp() throws Exception {
        handler = new WsPlainTextCallbackHandler(userName, password);
    }


    @Test
    public void testHandle_NameCallback_usernameIsSet() throws Exception {
        Callback[] callbacks = new Callback[1];
        NameCallback nameCallback = new NameCallback("username");
        callbacks[0] = nameCallback;

        handler.handle(callbacks);
        assertEquals(userName, nameCallback.getName());
    }

    @Test
    public void testHandle_NameCallback_passwordIsSet() throws Exception {
        Callback[] callbacks = new Callback[1];
        PasswordCallback passwordCallback = new PasswordCallback("password", false);
        callbacks[0] = passwordCallback;

        handler.handle(callbacks);
        assertEquals(password, new String(passwordCallback.getPassword()));
    }

    @Test(expected = UnsupportedCallbackException.class)
    public void gooitFoutIndienCallbackNietGekend() throws Exception {
        handler.handle(new Callback[]{new LanguageCallback()});
    }

}
