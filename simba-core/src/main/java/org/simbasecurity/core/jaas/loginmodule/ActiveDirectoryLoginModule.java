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

import org.owasp.esapi.Encoder;
import org.owasp.esapi.reference.DefaultEncoder;
import org.simbasecurity.common.constants.AuthenticationConstants;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.domain.Group;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.locator.GlobalContext;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.Hashtable;
import java.util.Map;

/**
 * This {@link javax.security.auth.spi.LoginModule LoginModule} performs Active Directory based authentication. A
 * username and password is verified against the corresponding user credentials stored in an Active Directory. This
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
public class ActiveDirectoryLoginModule extends SimbaLoginModule {

    private static final String KEY_SEARCH_FILTER = "filter";
    private static final String KEY_AUTHENTICATION_ATTR = "authAttr";
    private static final String KEY_AUTHENTICATION_DOMAIN = "authDomain";
    private static final String KEY_PRIMARY_SERVER = "primaryServer";
    private static final String KEY_BASE_DN = "baseDN";
    private static final String KEY_SECONDARY_SERVER = "secondaryServer";
    private static final String KEY_SECURITY_LEVEL = "securityLevel";
    private static final String KEY_SEARCH_SCOPE = "searchScope";

    private static final int NAME_CALLBACK = 0;
    private static final int PASSWORD_CALLBACK = 1;

    private Callback[] callbacks;

    private String primaryServerHost;
    private int primaryServerPort;
    private String secondaryServerHost;
    private int secondaryServerPort;
    private String authenticationDomain;
    private String searchBase;
    private String authenticationAttribute;
    private String searchFilter;
    private String securityLevel;
    private int searchScope;
    private String userCN;

    public ActiveDirectoryLoginModule() {
        super();
        callbacks = new Callback[2];
        callbacks[NAME_CALLBACK] = new NameCallback(AuthenticationConstants.USERNAME);
        callbacks[PASSWORD_CALLBACK] = new PasswordCallback(AuthenticationConstants.PASSWORD, false);
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);

        primaryServerHost = (String) options.get(KEY_PRIMARY_SERVER);
        primaryServerPort = 389;
        int colonIndex = primaryServerHost.indexOf(':');
        if (colonIndex != -1) {
            primaryServerPort = Integer.parseInt(primaryServerHost.substring(colonIndex + 1));
            primaryServerHost = primaryServerHost.substring(0, colonIndex);
        }

        secondaryServerHost = (String) options.get(KEY_SECONDARY_SERVER);
        if (secondaryServerHost != null) {
            secondaryServerPort = 389;
            colonIndex = secondaryServerHost.indexOf(':');
            if (colonIndex != -1) {
                secondaryServerPort = Integer.parseInt(secondaryServerHost.substring(colonIndex + 1));
                secondaryServerHost = secondaryServerHost.substring(0, colonIndex);
            }
        }

        authenticationDomain = (String) options.get(KEY_AUTHENTICATION_DOMAIN);
        searchBase = (String) options.get(KEY_BASE_DN);
        authenticationAttribute = (String) options.get(KEY_AUTHENTICATION_ATTR);
        searchFilter = (String) options.get(KEY_SEARCH_FILTER);
        securityLevel = (String) options.get(KEY_SECURITY_LEVEL);

        String scope = (String) options.get(KEY_SEARCH_SCOPE);

        if (scope == null || "subtree".equalsIgnoreCase(scope)) {
            searchScope = SearchControls.SUBTREE_SCOPE;
        } else if ("object".equalsIgnoreCase(scope)) {
            searchScope = SearchControls.OBJECT_SCOPE;
        } else if ("onelevel".equalsIgnoreCase(scope)) {
            searchScope = SearchControls.ONELEVEL_SCOPE;
        } else {
            debug("Invalid search scope provided. Using sub-tree scope");
        }
    }

    private void updateUserGroups(LdapContext ldapContext) {
        UserRepository userRepository = GlobalContext.locate(UserRepository.class);
        User user = userRepository.findByName(getUsername());
        if(user != null) {
            user.clearGroups();
            try {
                addADGroupsToUser(ldapContext, user);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void addADGroupsToUser(LdapContext ldapContext, User user) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(new String[] { "dn"});
        searchControls.setSearchScope(searchScope);

        GroupRepository groupRepository = GlobalContext.locate(GroupRepository.class);
        Encoder encoder = DefaultEncoder.getInstance();
        String filterGroups = encoder.encodeForLDAP("(&(member="+userCN+","+searchBase+")(objectcategory=group))");

        NamingEnumeration results = ldapContext.search(encoder.encodeForLDAP(searchBase), filterGroups, searchControls);
        while (hasMoreResults(results)) {
            String groupCN = ((SearchResult) results.next()).getName();
            Group group = groupRepository.findByCN(groupCN);
            if(group!=null) {
                user.addGroup(group);
            }
        }
    }

    private boolean hasMoreResults(NamingEnumeration ne) {
        try {
            return ne.hasMore();
        } catch (NamingException e) {
            return false;
        }
    }

    @Override
    protected boolean verifyLoginData() throws FailedLoginException {
        String[] returnedAtts = {authenticationAttribute};
        String requestSearchFilter = searchFilter.replaceAll("%USERNAME%", getUsername());

        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(returnedAtts);
        searchCtls.setSearchScope(searchScope);

        Hashtable<String, String> env = getEnv();

        debug("Verifying credentials for user: " + getUsername());

        boolean ldapUser = false;

        try {
            LdapContext ldapContext = getLdapContext(env);
            if (ldapContext != null) {
                Encoder encoder = DefaultEncoder.getInstance();
                NamingEnumeration<SearchResult> answer = ldapContext.search(encoder.encodeForLDAP(searchBase), encoder.encodeForLDAP(requestSearchFilter), searchCtls);

                while (!ldapUser && answer.hasMoreElements()) {
                    SearchResult sr = answer.next();
                    userCN = sr.getName();
                    Attributes attrs = sr.getAttributes();
                    if (attrs != null) {
                        NamingEnumeration<? extends Attribute> ne = attrs.getAll();
                        ldapUser = ne.hasMore();
                        ne.close();
                    }
                }
                debug("Authentication succeeded");
                if(Boolean.TRUE.equals(GlobalContext.locate(ConfigurationService.class).getValue(ConfigurationParameter.ENABLE_AD_GROUPS))) {
                    updateUserGroups(ldapContext);
                }
            }
            return ldapUser;
        } catch (NamingException ex) {
            debug("Authentication failed");
            throw new FailedLoginException(ex.getMessage());
        }
    }

    private Hashtable<String, String> getEnv() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        if (securityLevel != null) {
            env.put(Context.SECURITY_AUTHENTICATION, securityLevel);
        }
        env.put(Context.SECURITY_PRINCIPAL, getUsername() + "@" + authenticationDomain);
        env.put(Context.SECURITY_CREDENTIALS, getPassword());
        return env;
    }

    private LdapContext getLdapContext(Hashtable<String, String> env) {
        LdapContext ldapContext = tryPrimaryContext(env);
        if (ldapContext == null && secondaryServerHost != null) {
            ldapContext = trySecondaryContext(env);
        }
        return ldapContext;
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

    private LdapContext tryPrimaryContext(Hashtable<String, String> env) {
        env.put(Context.PROVIDER_URL, "ldap://" + primaryServerHost + ":" + primaryServerPort);
        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            debug("Authentication against primary server failed...");
            return null;
        }
    }

    private LdapContext trySecondaryContext(Hashtable<String, String> env) {
        debug("Trying secondary server...");
        env.put(Context.PROVIDER_URL, "ldap://" + secondaryServerHost + ":" + secondaryServerPort);
        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            debug("Authentication against secondary server failed...");
            return null;
        }
    }
}
