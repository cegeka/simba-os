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
package org.simbasecurity.core.chain.session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.domain.SessionEntity;
import org.simbasecurity.core.domain.User;

@RunWith(MockitoJUnitRunner.class)
public class EnterApplicationCommandTest {

    @Mock private ChainContext chainContextMock;
    @Mock private Audit auditMock;

    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks
    private EnterApplicationCommand enterApplicationCommand;

    @Test
    public void doFilterAndFinishOnExecute() throws Exception {
        SSOToken ssoToken = new SSOToken();
        when(chainContextMock.getRequestSSOToken()).thenReturn(ssoToken);
        User userMock = mock(User.class);
        when(userMock.getUserName()).thenReturn("userName");
        SessionEntity sessionEntity = new SessionEntity(userMock, ssoToken, "0.0.0.0", "0.0.0.0");
        when(chainContextMock.getCurrentSession()).thenReturn(sessionEntity);

        State state = enterApplicationCommand.execute(chainContextMock);

        verify(chainContextMock).activateAction(ActionType.DO_FILTER_AND_SET_PRINCIPAL);
        assertEquals(State.FINISH, state);
    }

}
