package org.simbasecurity.core.chain.usermanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserForTest;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.ResetPasswordMailService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.chain.Command.State.FINISH;
import static org.simbasecurity.core.domain.UserForTest.UserTestBuilder.aUser;

@RunWith(MockitoJUnitRunner.class)
public class SendEmailCommandTest {

    @Mock
    private ChainContext chainContextMock;

    @Mock
    private CredentialService credentialServiceMock;

    @Mock
    private ResetPasswordMailService resetPasswordMailServiceMock;

    @InjectMocks
    private SendEmailCommand sendEmailCommand;

    @Test
    public void execute_EmailKnown_WillSendMailAndRedirect() throws Exception {
        User user = aUser().build();
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail"));
        when(credentialServiceMock.findUserByMail("someEmail")).thenReturn(Optional.ofNullable(user));

        Command.State state = sendEmailCommand.execute(chainContextMock);

        verify(resetPasswordMailServiceMock).sendMessage(user);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
    }

    @Test
    public void execute_EmailUnknown_WillNotSendMailButWillRedirect() throws Exception {
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail"));
        when(credentialServiceMock.findUserByMail("someEmail")).thenReturn(Optional.empty());

        Command.State state = sendEmailCommand.execute(chainContextMock);

        verifyZeroInteractions(resetPasswordMailServiceMock);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
    }
}
