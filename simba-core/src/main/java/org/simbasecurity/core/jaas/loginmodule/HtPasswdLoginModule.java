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
package org.simbasecurity.core.jaas.loginmodule;

import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.jaas.loginmodule.htpasswd.*;
import org.simbasecurity.core.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link javax.security.auth.spi.LoginModule LoginModule} performs htpasswd based authentication.
 * <p/>
 * A username
 * and password is verified against the corresponding user credentials stored in an Active Directory. This
 * module requires the supplied {@link javax.security.auth.callback.CallbackHandler CallbackHandler} to support a
 * {@link javax.security.auth.callback.NameCallback NameCallback} and a
 * {@link javax.security.auth.callback.PasswordCallback PasswordCallback}.
 * <p/>
 * The following options are mandatory and must be specified in this module's login {@link
 * javax.security.auth.login.Configuration Configuration}:
 * <dl><dt></dt><dd>
 * <dl><dt><code>primaryServer=<b>&lt;server&gt;[:&lt;port&gt;]</b></code></dt>
 * <dd> This option identifies the primary Active Directory server that stores the user entries. Configuring the port
 * is optional. If the port is not specified the ldap default port number (389) is used.</dd>
 * <p/>
 * <dt><code>authDomain=<b>domain</b></code></dt>
 * <dd> This option specifies the user's domain to authenticate against. </dd>
 * <p/>
 * <dt><code>baseDN=<b>ldap_query</b></code></dt>
 * <dd> This option specifies the base directory node from which to start searching for a user. </dd>
 * <p/>
 * <dt><code>filter=<b>ldap_filter</b></code></dt>
 * <dd> This option specifies the ldap filter to use when searching the user's directory entry. </dd>
 * <p/>
 * <dt><code>searchScope=<b>scope</b></code></dt>
 * <dd> This option specifies the search scope. The scope can be one of: <code>"subtree"</code>, <code>"object"</code>
 * or <code>"onelevel"</code>.
 * <p/>
 * <dt><code>authAttr=<b>attribute</b></code></dt>
 * <dd> This option specifies the attribute to retrieve from the user's directory entry. </dd>
 * </dl></dl>
 * <p/>
 * This module also recognizes the following optional {@link javax.security.auth.login.Configuration Configuration}
 * options:
 * <dl><dt></dt><dd>
 * <dl><dt><code>secondaryServer=<b>&lt;server&gt;[:&lt;port&gt;]</b></code></dt>
 * <dd> This option identifies the secondary Active Directory server that stores the user entries. Configuring the port
 * is optional. If the port is not specified the ldap default port number (389) is used.</dd>
 * <p/>
 * <dt><code>securityLevel=<b>level</b></code></dt>
 * <dd> This options specifies the security level to use. The level should be one of the following: <code>"none"</code>,
 * <code>"simple"</code> or <code>"strong"</code>. If this property is unspecified, the behaviour is determined by
 * the service provider. </dd>
 * <p/>
 * <dt><code>debug=<b>boolean</b></code></dt>
 * <dd> if <code>true</code>, debug messages are displayed using standard logging at
 * {@link java.util.logging.Level#FINE FINE} level. </dd>
 * </dl></dl>
 *
 * @since 1.0
 */
public class HtPasswdLoginModule extends SimbaLoginModule {

    private static final String KEY_MODE = "mode";
    private static final String KEY_FILE_LOCATION = "location";
    private static final String KEY_MOVE = "move";

    private static final int NAME_CALLBACK = 0;
    private static final int PASSWORD_CALLBACK = 1;

    /**
     * Ordered list of encryption formats. Hashes are checked in this order
     */
    private static final List<HtPasswdCrypt> CRYPTS = Arrays.asList(
            new BlowfishCrypt(),
            new MD5Crypt(),
            new SHA1Crypt(),
            new UnixCrypt(),
            new PlainTextCrypt()
    );

    private Callback[] callbacks;

    private Mode mode;
    private Map<String, String> htpasswdStorage;
    private boolean move = true;

    private CredentialService credentialService;

    public HtPasswdLoginModule() {
        super();
        callbacks = new Callback[2];
        callbacks[NAME_CALLBACK] = new NameCallback(AuthenticationConstants.USERNAME);
        callbacks[PASSWORD_CALLBACK] = new PasswordCallback(AuthenticationConstants.PASSWORD, false);
    }

    @Autowired
    public void setCredentialService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        String modeOption = (String) options.get(KEY_MODE);
        if (modeOption == null || "simba".equals(modeOption)) {
            mode = Mode.SIMBA;
        } else if ("file".equalsIgnoreCase(modeOption)) {
            mode = Mode.FILE;
        } else {
            mode = Mode.SIMBA;
            debug("Invalid mode option provided. Using simba mode");
        }

        if (mode == Mode.FILE) {
            String fileOption = (String) options.get(KEY_FILE_LOCATION);
            File htpasswdFile;
            if (fileOption != null) {
                htpasswdFile = new File(fileOption);
                if (!htpasswdFile.exists()) {
                    throw new IllegalArgumentException(htpasswdFile.getAbsolutePath() + " does not exist");
                }
            } else {
                htpasswdFile = new File("/usr/local/apache/passwd");
                debug("No htpasswd file location provided. Using /usr/local/apache/passwd");
                if (!htpasswdFile.exists()) {
                    throw new IllegalStateException("Could not use default /usr/local/apache/passwd file as it does not exist");
                }
            }

            htpasswdStorage = new HashMap<String, String>();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(htpasswdFile));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    line = line.trim();
                    int colonIndex = line.indexOf(':');
                    if (colonIndex > 0) { // TODO: Comments?
                        String username = line.substring(0, colonIndex);
                        String password = line.substring(colonIndex + 1);
                        htpasswdStorage.put(username, password);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("Cound not read htpasswd file (" + htpasswdFile.getAbsoluteFile(), e);
            } finally {
                try { reader.close(); } catch (Exception ignore) {}
            }
        }

        if (options.containsKey(KEY_MOVE)) {
            move = Boolean.valueOf((String) options.get(KEY_MOVE));
        }
    }

    @Override
    protected boolean verifyLoginData() throws FailedLoginException {
        debug("Verifying credentials for user: " + getUsername());

        boolean validCredentials = false;

        String storedHash;
        if (mode == Mode.FILE) {
            storedHash = htpasswdStorage.get(getUsername());
        } else {
            storedHash = credentialService.getPasswordHash(getUsername());
        }

        if (storedHash != null) {
            for (int i = 0; !validCredentials && i < CRYPTS.size(); i++) {
                HtPasswdCrypt crypt = CRYPTS.get(i);
                validCredentials = crypt.accepts(this, storedHash) && crypt.checkPassword(this, storedHash, getPassword());
            }
        }

        if (validCredentials) {
            debug("Authentication succeeded");
            if (move) {
                credentialService.changePassword(getUsername(), getPassword(), getPassword());
            }
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
        } catch (IOException ioe) {
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

    @Override
    public void debug(String message) {
        super.debug(message);
    }

    protected void setCallBacks(Callback[] callbacks) {
           this.callbacks = callbacks;
       }

    private static enum Mode {
        FILE,
        SIMBA
    }
}
