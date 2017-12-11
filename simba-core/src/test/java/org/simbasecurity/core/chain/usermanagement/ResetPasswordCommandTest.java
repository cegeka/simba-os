package org.simbasecurity.core.chain.usermanagement;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.chain.Command.State.FINISH;
import static org.simbasecurity.core.domain.UserTestBuilder.aUser;
import static org.simbasecurity.core.domain.user.EmailAddress.email;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordCommandTest {

    @Mock
    private ChainContext chainContextMock;

    @Mock
    private CredentialService credentialServiceMock;

    @Mock
    private ResetPasswordService resetPasswordServiceMock;

    @InjectMocks
    private ResetPasswordCommand resetPasswordCommand;

    @Test
    public void execute_EmailKnown_WillSendMailAndRedirect() throws Exception {
        User user = aUser().build();
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail@bla.com"));
        when(credentialServiceMock.findUserByMail(email("someEmail@bla.com"))).thenReturn(Optional.ofNullable(user));

        Command.State state = resetPasswordCommand.execute(chainContextMock);

        verify(resetPasswordServiceMock).sendResetPasswordMessageTo(user, ResetPasswordReason.FORGOT_PASSWORD);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
    }

    @Test
    public void execute_EmailUnknown_WillNotSendMailButWillRedirect() throws Exception {
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail@bla.com"));
        when(credentialServiceMock.findUserByMail(email("someEmail@bla.com"))).thenReturn(Optional.empty());

        Command.State state = resetPasswordCommand.execute(chainContextMock);

        verifyZeroInteractions(resetPasswordServiceMock);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
    }

    @Test
    @Ignore
    public void postProcess_IfInvalidEmailAddressException_ThenRedirectWithInvalidEmailParam() throws Exception {
        throw new Exception("Implement me");
    }
}
