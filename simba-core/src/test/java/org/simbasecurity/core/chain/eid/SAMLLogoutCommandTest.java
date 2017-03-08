package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SAMLLogoutCommandTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private ChainContext chainContext;
    @Mock private SAMLService samlService;
    @Mock private SAMLResponseHandler samlResponseHandler;

    @InjectMocks private SAMLLogoutCommand samlLogoutCommand;

    @Before
    public void setUp() throws Exception {
        when(samlService.getSAMLResponseHandler(null, null)).thenReturn(samlResponseHandler);
    }

    @Test
    public void testExecute_noSAMLLogoutResponse() throws Exception {
        when(samlResponseHandler.isLogoutResponse()).thenReturn(false);

        State state = samlLogoutCommand.execute(chainContext);

        assertEquals(State.CONTINUE, state);
    }

    @Test
    public void testExecute_SAMLLogoutResponse() throws Exception {
        when(samlResponseHandler.isLogoutResponse()).thenReturn(true);

        State state = samlLogoutCommand.execute(chainContext);

        verify(chainContext).redirectToLogout();
        assertEquals(State.FINISH, state);
    }
}