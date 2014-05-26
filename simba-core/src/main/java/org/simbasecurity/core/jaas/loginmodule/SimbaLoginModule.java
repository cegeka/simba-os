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
package org.simbasecurity.core.jaas.loginmodule;

import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sun.security.auth.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimbaLoginModule implements LoginModule {

    private static final Logger LOG = LoggerFactory.getLogger(SimbaLoginModule.class);

    private Subject subject;
    protected Map<String, ?> sharedState;
    protected Map<String, ?> options;

    protected String username;
    protected String password;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    private Principal userPrincipal;
    private CallbackHandler callbackHandler;

    private boolean debug;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.sharedState = sharedState;
        this.options = options;
        this.callbackHandler = callbackHandler;

        this.debug = "true".equalsIgnoreCase((String) options.get("debug"));
    }

    @Override
    public boolean abort() throws LoginException {
        debug("Aborted authentication");
        if (!succeeded) {
            return false;
        } else if (succeeded && !commitSucceeded) {
            // login succeeded but overall authentication failed
            succeeded = false;
            username = null;
            password = null;
            userPrincipal = null;

        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!succeeded) {
            return false;
        }
        userPrincipal = new UserPrincipal(username);
        getSubject().getPrincipals().add(userPrincipal);
        commitSucceeded = true;

        username = null;
        password = null;
        return true;
    }


    @Override
    public boolean login() throws LoginException {
        succeeded = false;
        getLoginDataFromUser();
        succeeded = verifyLoginData();

        return succeeded;
    }

    /**
     * Get the login data (username, password) from the CallbackHandler.
     *
     * @throws javax.security.auth.login.LoginException if a callback wasn't available (UnsupportedCallbackException)
     *                        or an IOException occurred
     */
    protected abstract void getLoginDataFromUser() throws LoginException;

    /**
     * Check if the username and password is correct. How the check is done, is
     * up to you.
     *
     * @return boolean true: login succeeded ; false:loginmodule ignored.
     * @throws javax.security.auth.login.LoginException when check on username and password is incorrect
     */
    protected abstract boolean verifyLoginData() throws LoginException;

    @Override
    public boolean logout() throws LoginException {
        getSubject().getPrincipals().remove(userPrincipal);

        succeeded = false;
        commitSucceeded = false;
        username = null;
        password = null;
        userPrincipal = null;
        return true;
    }

    /**
     * Get the authenticated subject.
     *
     * @return Subject
     */
    public Subject getSubject() {
        return subject;
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getPassword() {
        return password;
    }

    protected boolean isSucceeded() {
        return succeeded;
    }

    protected boolean isCommitSucceeded() {
        return commitSucceeded;
    }

    protected CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }

    protected void debug(String message) {
        if (debug) {
            LOG.debug(message);
        }
    }

}
