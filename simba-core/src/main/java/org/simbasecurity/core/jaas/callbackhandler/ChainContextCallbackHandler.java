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
package org.simbasecurity.core.jaas.callbackhandler;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.chain.ChainContext;

/**
 * Uses a {@link ChainContext} to get the credentials from the user. The username will
 * be put in the NameCallback and the password in the PasswordCallback. The
 * user data is now retrieved from a FORM using the getParameter method with as
 * arguments AuthenticationConstants.LOGIN_USERNAME and
 * AuthenticationConstants.LOGIN_PASSWORD.
 *
 * @since 1.0
 */
public class ChainContextCallbackHandler implements CallbackHandler {

    private ChainContext chainContext;

    /**
     * Constructor which takes the context to retrieve the credentials the
     * user submitted.
     *
     * @param chainContext the context containing user data
     */
    public ChainContextCallbackHandler(ChainContext chainContext) {
        this.chainContext = chainContext;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        getFormCredentials(callbacks);
    }

    private void getFormCredentials(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) callback;
                String login = chainContext.getUserName();
                nameCallback.setName(login);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                String password = chainContext.getRequestParameter(AuthenticationConstants.PASSWORD);
                passwordCallback.setPassword(null);
                if (password != null) {
                    passwordCallback.setPassword(password.toCharArray());
                }
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }

}
