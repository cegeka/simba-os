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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.test.AutowirerRule;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ActiveDirectoryLoginModuleTest {
    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Rule public AutowirerRule autowirer = AutowirerRule.autowirer();

    @Mock private CoreConfigurationService configurationService;

    @Before
    public void setUp() {
        autowirer.registerBean(configurationService);
        autowirer.mockBean(UserRepository.class);
        autowirer.mockBean(GroupRepository.class);
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
