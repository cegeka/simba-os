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

package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;
import org.simbasecurity.core.service.LoginMappingService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.FINISH;

public class SAMLAuthResponseCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String LOGIN_TOKEN_1234 = "loginToken1234";

    @Mock private SAMLService samlService;
    @Mock private ChainContext chainContext;
    @Mock private SAMLResponseHandler samlResponse;
    @Mock private LoginMappingService loginMappingService;
    @Mock private LoginMappingEntity loginMapping;
    @Mock private Audit audit;
    @Mock private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private SAMLAuthResponseCommand samlAuthResponseCommand;

    private final ArgumentCaptor<SAMLUser> samlUserCaptor = ArgumentCaptor.forClass(SAMLUser.class);

    @Before
    public void setup() throws Exception {
        when(samlService.getSAMLResponseHandler(null, null)).thenReturn(samlResponse);
    }

    @Test
    public void testExecute_noLoginMappingFound_redirectToAccessDenied() throws Exception {
        when(samlResponse.getInResponseTo()).thenReturn(LOGIN_TOKEN_1234);
        when(loginMappingService.getMapping(LOGIN_TOKEN_1234)).thenReturn(null);

        assertEquals(FINISH, samlAuthResponseCommand.execute(chainContext));

        verify(chainContext).redirectToAccessDenied();
    }

    @Test
    public void testExecute_loginMappingExpired_redirectToAccessDenied() throws Exception {
        when(samlResponse.getInResponseTo()).thenReturn(LOGIN_TOKEN_1234);
        when(loginMapping.isExpired()).thenReturn(true);
        when(loginMappingService.getMapping(LOGIN_TOKEN_1234)).thenReturn(loginMapping);

        assertEquals(FINISH, samlAuthResponseCommand.execute(chainContext));

        verify(chainContext).redirectToAccessDenied();
    }

    @Test
    public void testExecute_loginMappingValid_samlUserAndLoginMappingOnContext() throws Exception {
        when(samlResponse.getInResponseTo()).thenReturn(LOGIN_TOKEN_1234);
        when(samlResponse.getAttribute("egovNRN")).thenReturn("insz");
        when(samlResponse.getAttribute("givenName")).thenReturn("firstname");
        when(samlResponse.getAttribute("surname")).thenReturn("lastname");
        when(samlResponse.getAttribute("mail")).thenReturn("email");
        when(samlResponse.getAttribute("PrefLanguage")).thenReturn("language");
        when(loginMapping.isExpired()).thenReturn(false);
        when(loginMappingService.getMapping(LOGIN_TOKEN_1234)).thenReturn(loginMapping);

        assertEquals(CONTINUE, samlAuthResponseCommand.execute(chainContext));

        verify(chainContext).setLoginMapping(loginMapping);
        verify(chainContext).setSAMLUser(samlUserCaptor.capture());
        assertEquals("insz", samlUserCaptor.getAllValues().get(0).getInsz());
        assertEquals("firstname", samlUserCaptor.getAllValues().get(0).getFirstname());
        assertEquals("lastname", samlUserCaptor.getAllValues().get(0).getLastname());
        assertEquals("email", samlUserCaptor.getAllValues().get(0).getEmail());
        assertEquals("language", samlUserCaptor.getAllValues().get(0).getLanguage());

    }
}