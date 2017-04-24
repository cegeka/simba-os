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
package org.simbasecurity.core.chain.session;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.domain.Session;

@RunWith(MockitoJUnitRunner.class)
public class CreateCookieForNewSSOTokenCommandTest {

    @Mock private ChainContext chainContextMock;
    @Mock private Session session;

    @Test
    public void testExecute_DoesNothingWhenNoNewSSOToken() throws Exception {
        when(chainContextMock.isSsoTokenMappingKeyProvided()).thenReturn(false);
        CreateCookieForNewSSOTokenCommand command = new CreateCookieForNewSSOTokenCommand();

        command.execute(chainContextMock);

        verify(chainContextMock, never()).activateAction(any(ActionType.class));
        verify(chainContextMock, never()).setSSOTokenForActions(any(SSOToken.class));
    }

    @Test
    public void testExecute_ActivatesMakeCookieActionWhenNewSSOToken() throws Exception {
        SSOToken ssoToken = new SSOToken();
        when(chainContextMock.isSsoTokenMappingKeyProvided()).thenReturn(true);
        when(chainContextMock.getCurrentSession()).thenReturn(session);
        when(session.getSSOToken()).thenReturn(ssoToken);

        CreateCookieForNewSSOTokenCommand command = new CreateCookieForNewSSOTokenCommand();

        command.execute(chainContextMock);

        verify(chainContextMock).activateAction(ActionType.MAKE_COOKIE);
        verify(chainContextMock).setSSOTokenForActions(ssoToken);
    }
}
