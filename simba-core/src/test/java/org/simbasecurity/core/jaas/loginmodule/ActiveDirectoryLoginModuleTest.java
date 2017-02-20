package org.simbasecurity.core.jaas.loginmodule;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.repository.GroupRepository;
import org.simbasecurity.core.jaas.callbackhandler.ChainContextCallbackHandler;
import org.simbasecurity.test.LocatorTestCase;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ActiveDirectoryLoginModuleTest extends LocatorTestCase {
    @Test
    @Ignore // TODO: bkbac: Mock LDAP Server
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

        module.addADGroupsToUser(ldapContext, mock(User.class));

        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        verify(ldapContext).search(captor1.capture(), captor2.capture(), any(SearchControls.class));

        assertFalse("LDAP injection possible", captor1.getValue().contains("*"));
        assertFalse("LDAP injection possible", captor2.getValue().contains("*"));
    }
}
