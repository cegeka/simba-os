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
package org.simbasecurity.core.chain;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @since 1.0
 */
public class ChainImplTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock private ChainContext contextMock;
    @Mock private Audit auditMock;
    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private ChainImpl chain;

    @Test(expected = NullPointerException.class)
    public void testExecuteThrowsNullPointerOnNullContext() throws Exception {
        chain.setCommands(Collections.<Command>emptyList());
        chain.execute(null);
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteReThrowsExceptionWhenNotHandled() throws Exception {
        Command exceptionCommand = mock(Command.class);

        chain.setCommands(Collections.singletonList(exceptionCommand));

        when(exceptionCommand.execute(contextMock)).thenThrow(new RuntimeException());

        chain.execute(contextMock);

        verifyNoMoreInteractions(exceptionCommand);
    }

    @Test
    public void testExecuteProcessesCompleteChain_Error() throws Exception {
        Command command = mock(Command.class);
        Command otherCommand = mock(Command.class);

        chain.setCommands(Arrays.asList(command, otherCommand));

        when(command.execute(contextMock)).thenReturn(Command.State.ERROR);
        when(otherCommand.execute(contextMock)).thenReturn(Command.State.ERROR);

        assertEquals(Command.State.ERROR, chain.execute(contextMock));

        verify(command).execute(contextMock);
        verify(command).postProcess(contextMock, null);
        verify(otherCommand).execute(contextMock);
        verify(otherCommand).postProcess(contextMock, null);
        verifyNoMoreInteractions(command, otherCommand);
    }

    @Test
    public void testExecuteStopsChainWhenFinished() throws Exception {
        Command completeCommand = mock(Command.class);
        Command otherCommand = mock(Command.class);

        chain.setCommands(Arrays.asList(completeCommand, otherCommand));

        when(completeCommand.execute(contextMock)).thenReturn(Command.State.FINISH);

        chain.execute(contextMock);

        verify(completeCommand).execute(contextMock);
        verify(completeCommand).postProcess(contextMock, null);
        verifyNoMoreInteractions(completeCommand, otherCommand);
    }

    @Test
    public void testChainReturnCorrectResultAfterHandledException() throws Exception {
        Command completeCommand = mock(Command.class);
        Command exceptionCommand = mock(Command.class);

        chain.setCommands(Arrays.asList(completeCommand, exceptionCommand));

        RuntimeException exception = new RuntimeException();

        when(completeCommand.execute(contextMock)).thenReturn(Command.State.FINISH);
        when(completeCommand.postProcess(contextMock, exception)).thenReturn(Boolean.TRUE);
        when(exceptionCommand.execute(contextMock)).thenThrow(exception);

        assertEquals(Command.State.FINISH, chain.execute(contextMock));

        verify(completeCommand).execute(contextMock);
        verify(completeCommand).postProcess(contextMock, null);
        verifyNoMoreInteractions(completeCommand, exceptionCommand);
    }

}
