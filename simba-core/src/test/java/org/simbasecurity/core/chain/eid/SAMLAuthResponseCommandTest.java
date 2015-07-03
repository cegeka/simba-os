package org.simbasecurity.core.chain.eid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.saml.SAMLResponseHandler;
import org.simbasecurity.core.saml.SAMLService;
import org.simbasecurity.core.service.LoginMappingService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.FINISH;

@RunWith(MockitoJUnitRunner.class)
public class SAMLAuthResponseCommandTest {

    public static final String LOGIN_TOKEN_1234 = "loginToken1234";
    @InjectMocks private SAMLAuthResponseCommand samlAuthResponseCommand;

    @Mock private SAMLService samlService;

    @Mock private ChainContext chainContext;

    @Mock private SAMLResponseHandler samlResponse;

    @Mock private LoginMappingService loginMappingService;

    @Mock private LoginMappingEntity loginMapping;

    private final ArgumentCaptor<SAMLUser> samlUserCaptor = ArgumentCaptor.forClass(SAMLUser.class);

    @Before
    public void setup() throws Exception {
        when(samlService.getSAMLResponseHandler(anyString(), anyString())).thenReturn(samlResponse);
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