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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.saml.SAMLService;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.common.constants.AuthenticationConstants.LOGIN_TOKEN;

@RunWith(MockitoJUnitRunner.class)
public class EIDCheckSessionCommandTest {

    @InjectMocks
    private EIDCheckSessionCommand command;

    @Mock
    private ChainContext contextMock;

    @Mock
    private SAMLService samlService;

    @Mock
    private AuditLogEventFactory auditLogEventFactory;

    @Mock
    private Audit audit;

    private final ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
    private final ArgumentCaptor<Map> parametersCaptor = ArgumentCaptor.forClass(Map.class);

    @Test
    public void redirectWhenNoSSOToken() throws Exception {
        when(contextMock.getRequestSSOToken()).thenReturn(null);

        LoginMappingEntity loginMapping = new LoginMappingEntity();
        when(contextMock.createLoginMapping()).thenReturn(loginMapping);
        when(samlService.getAuthRequestUrl(eq(loginMapping.getToken()), any(Date.class))).thenReturn("SAMLAuthRequestURL");
        assertEquals(Command.State.FINISH, command.execute(contextMock));

        verify(contextMock).redirectWithParameters(urlCaptor.capture(), parametersCaptor.capture());
        verify(samlService).getAuthRequestUrl(eq(loginMapping.getToken()), any(Date.class));
        assertEquals("SAMLAuthRequestURL", urlCaptor.getAllValues().get(0));
        assertEquals(loginMapping.getToken(), parametersCaptor.getAllValues().get(0).get(LOGIN_TOKEN));
    }
}