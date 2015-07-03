package org.simbasecurity.core.chain.eid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command.State;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SAMLLogoutCommandTest {

    @Mock private ChainContext chainContext;
    @Mock private SAMLService samlService;
    @Mock private SAMLResponseHandler samlResponseHandler;

    @InjectMocks private SAMLLogoutCommand samlLogoutCommand;

    @Before
    public void setUp() throws Exception {
        when(samlService.getSAMLResponseHandler(anyString(), anyString())).thenReturn(samlResponseHandler);
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