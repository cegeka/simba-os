package org.simbasecurity.core.chain.usermanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserTestBuilder;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.token.UserTokenService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.FINISH;

@RunWith(MockitoJUnitRunner.class)
public class CheckTokenCommandTest {


    @Mock
    private ChainContext chainContextMock;
    @Mock
    private UserTokenService userTokenServiceMock;
    @Mock
    private CredentialService credentialServiceMock;

    @InjectMocks
    private CheckTokenCommand checkTokenCommand;

    @Test
    public void execute_withoutTokenInDatabank_statusError() throws Exception {
        when(chainContextMock.getToken()).thenReturn(Optional.of("someUUID"));
        when(userTokenServiceMock.getUserForToken(Token.fromString("someUUID"))).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
    }

    @Test
    public void execute_withoutTokenInContext_statusError() throws Exception {
        when(chainContextMock.getToken()).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
    }

    @Test
    public void execute_withTokenInContextAndDatabase_butUnknownEmailAddress_statusError() throws Exception {
        User user = UserTestBuilder.aDefaultUser().withEmail("bruce@wayneindustries.com").build();
        setupContextWith("bruce@wayneindustries.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("bruce@wayneindustries.com"))).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
    }

    @Test
    public void execute_withTokenInContextAndDatabase_butEmailAddressUserIsDifferentFromTokenUser_statusError() throws Exception {
        User user = UserTestBuilder.aDefaultUser().withId(185L).withEmail("bruce@wayneindustries.com").build();
        User snarf = UserTestBuilder.aDefaultUser().withId(665L).withEmail("snarf@lioncats.com").build();
        setupContextWith("snarf@lioncats.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("snarf@lioncats.com"))).thenReturn(Optional.of(snarf));

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
    }

    @Test
    public void execute_withTokenInContextAndDatabase_EmailAddressUserIsSameAsTokenUser_statusContinue() throws Exception {
        User user = UserTestBuilder.aDefaultUser().withEmail("bruce@wayneindustries.com").build();
        setupContextWith("bruce@wayneindustries.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("bruce@wayneindustries.com"))).thenReturn(Optional.of(user));

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(CONTINUE);
    }

    private void setupContextWith(String email, String token) {
        when(chainContextMock.getToken()).thenReturn(Optional.of(token));
        when(chainContextMock.getEmail()).thenReturn(Optional.of(email));
    }
}