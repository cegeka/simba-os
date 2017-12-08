package org.simbasecurity.core.chain.usermanagement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.token.UserTokenService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.audit.AuditMessages.PASSWORD_CHANGED;
import static org.simbasecurity.core.audit.AuditMessages.PASSWORD_NOT_VALID;
import static org.simbasecurity.core.chain.Command.State.FINISH;
import static org.simbasecurity.core.exception.SimbaMessageKey.PASSWORDS_DONT_MATCH;

@RunWith(MockitoJUnitRunner.class)
public class NewPasswordCommandTest {

    @Mock
    private ChainContext contextMock;
    @Mock
    private UserTokenService userTokenServiceMock;
    @Mock
    private Audit auditMock;
    @Mock
    private AuditLogEventFactory auditLogFactory;
    @Mock
    private CredentialService credentialServiceMock;

    @InjectMocks
    private NewPasswordCommand newPasswordCommand;

    @Test
    public void execute_NoNewPassword_ThenNavigateToNewPasswordPage() throws Exception {
        when(contextMock.getNewPassword()).thenReturn(Optional.empty());
        when(contextMock.getToken()).thenReturn(Optional.of("someToken"));

        Command.State state = newPasswordCommand.execute(contextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(contextMock).redirectToNewPassword("someToken", null);
    }

    @Test
    public void execute_NewPassword() throws Exception {
        when(contextMock.getNewPassword()).thenReturn(Optional.of("newPassword"));
        when(contextMock.getNewPasswordConfirmation()).thenReturn("newPassword");
        when(contextMock.getUserName()).thenReturn("someUsername");
        when(contextMock.getToken()).thenReturn(Optional.of("token"));
        AuditLogEvent auditLogEvent = mock(AuditLogEvent.class);
        when(auditLogFactory.createEventForSessionForSuccess(contextMock, PASSWORD_CHANGED)).thenReturn(auditLogEvent);

        Command.State state = newPasswordCommand.execute(contextMock);

        assertThat(state).isEqualTo(FINISH);

        verify(userTokenServiceMock).deleteToken(Token.fromString("token"));
        verify(credentialServiceMock).changePassword("someUsername", "newPassword", "newPassword");
        verify(auditMock).log(auditLogEvent);
        verify(contextMock).redirectToNewPasswordSuccessPage();
    }

    @Test
    public void execute_PasswordConfirmationIsDifferentThenPassword_ThrowsException() throws Exception {
        when(contextMock.getNewPassword()).thenReturn(Optional.of("newPassword"));
        when(contextMock.getNewPasswordConfirmation()).thenReturn("differentPassword");
        when(contextMock.getUserName()).thenReturn("someUsername");
        when(contextMock.getToken()).thenReturn(Optional.of("token"));
        doThrow(new SimbaException(PASSWORDS_DONT_MATCH)).when(credentialServiceMock).changePassword("someUsername", "newPassword","differentPassword");
        AuditLogEvent auditLogEvent = mock(AuditLogEvent.class);
        when(auditLogFactory.createEventForSessionForFailure(contextMock, PASSWORD_NOT_VALID)).thenReturn(auditLogEvent);

        Command.State state = newPasswordCommand.execute(contextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(credentialServiceMock).changePassword("someUsername", "newPassword","differentPassword");
        verify(auditMock).log(auditLogEvent);
        verify(contextMock).redirectToNewPassword("token", "PASSWORDS_DONT_MATCH");
    }

}