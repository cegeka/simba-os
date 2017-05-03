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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.jaas.callbackhandler.ChainContextCallbackHandler;
import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.locator.Locator;
import org.simbasecurity.test.LocatorTestCase;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ActiveDirectoryLoginModuleTest extends LocatorTestCase {
    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @Mock private ConfigurationService configurationService;

    @Before
    public void setUp() {
        Locator locator = mock(Locator.class);
        GlobalContext.initialize(locator);
        when(locator.locate(ConfigurationService.class)).thenReturn(configurationService);
    }

    @Test
    @Ignore
    public void testVerifyLoginData_NoLDAPInjectionPossible() throws Exception {
        ActiveDirectoryLoginModule module = new ActiveDirectoryLoginModule();
        ChainContextCallbackHandler mockCallbackHandler = mock(ChainContextCallbackHandler.class);
        mockCallbackHandler.handle(any(Callback[].class));
        Subject subject = new Subject();
        Map<String, String> options = new HashMap<>();
        options.put("filter", "test%USERNAME%");
        options.put("baseDN", "test*");
        options.put("primaryServer", "localhost:8080");
        LdapContext ldapContext = mock(LdapContext.class);
        when(ldapContext.search(anyString(), anyString(), any(SearchControls.class))).thenReturn(mock(NamingEnumeration.class));
        implantMock(GroupRepository.class);

        module.initialize(subject, mockCallbackHandler, Collections.emptyMap(), options);
        module.setUsername("*test");
        module.setPassword("pass");
        module.verifyLoginData();

        module.addADGroupsToUser(ldapContext, mock(User.class), "");

        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        verify(ldapContext).search(captor1.capture(), captor2.capture(), any(SearchControls.class));

        assertFalse("LDAP injection possible", captor1.getValue().contains("*"));
        assertFalse("LDAP injection possible", captor2.getValue().contains("*"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void injection() throws Exception {

        when(configurationService.getValue(SimbaConfigurationParameter.ENABLE_AD_GROUPS)).thenReturn(Boolean.FALSE);

        Map<String, String> options = new HashMap<>();
        options.put("primaryServer", "localhost:389");
        options.put("baseDN", "'dc=rsvzinasti,dc=be'");
        options.put("filter", "(&amp;(objectClass=person)(sAMAccountName=%USERNAME%))");
        options.put("searchScope", "subtree");
        options.put("authDomain", "rsvzinasti.be");
        options.put("authAttr", "sAMAccountName");
        options.put("securityLevel", "simple");

        NamingEnumeration attrsNamingEnumeration = mock(NamingEnumeration.class);
        when(attrsNamingEnumeration.hasMore()).thenReturn(true);

        Attributes attrs = mock(Attributes.class);
        when(attrs.getAll()).thenReturn(attrsNamingEnumeration);

        SearchResult searchResult = mock(SearchResult.class);
        when(searchResult.getName()).thenReturn(null);
        when(searchResult.getAttributes()).thenReturn(attrs);

        NamingEnumeration<SearchResult> searchResultNamingEnumeration = mock(NamingEnumeration.class);

        when(searchResultNamingEnumeration.hasMoreElements()).thenReturn(true).thenReturn(false);
        when(searchResultNamingEnumeration.next()).thenReturn(searchResult);

        ArgumentCaptor<String> searchFilter = ArgumentCaptor.forClass(String.class);

        final LdapContext ldapContext = mock(LdapContext.class);

        when(ldapContext.search(eq("'dc=rsvzinasti,dc=be'"), searchFilter.capture(), any(SearchControls.class)))
                .thenReturn(searchResultNamingEnumeration);

        ActiveDirectoryLoginModule loginModule = new ActiveDirectoryLoginModule() {
            @Override
            protected LdapContext tryPrimaryContext(Hashtable<String, String> env) {
                return ldapContext;
            }
        };

        loginModule.setUsername(" u\\*()\u0000 ");
        loginModule.setPassword(" p\\*()\u0000 ");
        loginModule.initialize(new Subject(), mock(CallbackHandler.class), Collections.emptyMap(), options);

        boolean result = loginModule.verifyLoginData();

        assertThat(result).isTrue();
        assertThat(searchFilter.getValue()).isEqualTo("(&amp;(objectClass=person)(sAMAccountName= u5c2a282900 ))");
    }

}
