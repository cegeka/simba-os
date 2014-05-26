/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.jaas.loginmodule;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.common.constants.AuthenticationConstants;

public class DatabaseLoginModule extends SimbaLoginModule {

    private static final int NAME_CALLBACK = 0;
    private static final int PASSWORD_CALLBACK = 1;

    private Callback[] callbacks;

    /**
     * Default constructor which will initialize the userData with users.
     */
    public DatabaseLoginModule() {
        super();
        callbacks = new Callback[2];
        callbacks[NAME_CALLBACK] = new NameCallback(AuthenticationConstants.USERNAME);
        callbacks[PASSWORD_CALLBACK] = new PasswordCallback(AuthenticationConstants.PASSWORD, false);
    }

    @Override
    protected boolean verifyLoginData() throws FailedLoginException {

        CredentialService credentialService = GlobalContext.locate(CredentialService.class);

        debug("Verifying credentials for user: " + getUsername());

        boolean validCredentials = false;

        try {
            validCredentials = credentialService.checkCredentials(getUsername(), getPassword());
        } catch (EncryptionOperationNotPossibleException legacyPasswordException) {
            debug("Authentication failed");
            throw new FailedLoginException(getUsername());
        }

        if (validCredentials) {
            debug("Authentication succeeded");
            return true;
        }

        debug("Authentication failed");
        throw new FailedLoginException(getUsername());
    }

    @Override
    protected void getLoginDataFromUser() throws LoginException {
        try {
            getCallbackHandler().handle(callbacks);
            setUsername(getNameFromCallback());
            setPassword(getPasswordFromCallback());
            resetPassword();
        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Callback error : " + uce.getCallback().toString()
                    + " not available to authenticate the user");
        }
    }

    private void resetPassword() {
        ((PasswordCallback) callbacks[PASSWORD_CALLBACK]).clearPassword();
    }

    private String getPasswordFromCallback() {
        return String.valueOf(((PasswordCallback) callbacks[PASSWORD_CALLBACK]).getPassword());
    }

    private String getNameFromCallback() {
        return ((NameCallback) callbacks[NAME_CALLBACK]).getName();
    }

    protected void setCallBacks(Callback[] callbacks) {
        this.callbacks = callbacks;
    }

}
