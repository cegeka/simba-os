package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.audit.*;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidateSAMLParametersCommandTest {

    @Mock
    private Audit auditMock;
    @Mock
    private ChainContext chainContextMock;
    @Mock
    private SAMLService samlServiceMock;
    @Mock
    private SAMLResponseHandler samlResponseHandlerMock;

    @Spy
    private AuditLogEventFactory auditLogEventFactory;

    private ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);

    @InjectMocks
    private ValidateSAMLParametersCommand command;


    @Before
    public void setUp() throws Exception {
        when(samlServiceMock.getSAMLResponseHandler(anyString(), anyString())).thenReturn(samlResponseHandlerMock);
    }

    @Test
    public void testExecute_Valid() throws Exception {
        when(samlResponseHandlerMock.isValid()).thenReturn(true);

        State state = command.execute(chainContextMock);

        verify(auditMock).log(captor.capture());
        AuditLogEvent resultAuditLogEvent = captor.getValue();

        assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
        assertEquals(AuditMessages.SUCCESS + AuditMessages.VALID_SAML_RESPONSE, resultAuditLogEvent.getMessage());

        assertEquals(State.CONTINUE, state);
    }

    @Test
    public void testExecute_Invalid() throws Exception {
        when(samlResponseHandlerMock.isValid()).thenReturn(false);

        State state = command.execute(chainContextMock);

        verify(chainContextMock).redirectToAccessDenied();

        verify(auditMock).log(captor.capture());
        AuditLogEvent resultAuditLogEvent = captor.getValue();

        assertEquals(AuditLogEventCategory.AUTHENTICATION, resultAuditLogEvent.getCategory());
        assertEquals(AuditMessages.FAILURE + AuditMessages.INVALID_SAML_RESPONSE, resultAuditLogEvent.getMessage());

        assertEquals(State.FINISH, state);
    }
}