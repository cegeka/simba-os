package org.simbasecurity.core.chain.usermanagement;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.reset.password.ForgotPassword;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.chain.Command.State.FINISH;
import static org.simbasecurity.core.domain.UserTestBuilder.aUser;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordCommandTest {

    @Mock private ChainContext chainContextMock;
    @Mock private CredentialService credentialServiceMock;
    @Mock private ResetPasswordService resetPasswordServiceMock;
    @Mock private ForgotPassword resetReason;
    @Mock private Audit audit;
    @Mock private AuditLogEventFactory auditLogEventFactory;
    @Mock private CoreConfigurationService coreConfigurationService;
    @Spy private EmailFactory emailFactory = StubEmailFactory.emailRequired();

    @InjectMocks private ResetPasswordCommand resetPasswordCommand;

    @Test
    public void execute_EmailKnown_WillSendMailAndRedirect() throws Exception {
        User user = aUser().build();
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail@bla.com"));
        when(credentialServiceMock.findUserByMail(emailFactory.email("someEmail@bla.com"))).thenReturn(Optional.ofNullable(user));
        AuditLogEvent auditLogEvent = mock(AuditLogEvent.class);
        when(auditLogEventFactory.createEventForUserAuthentication(eq(user.getName()), anyString())).thenReturn(auditLogEvent);

        Command.State state = resetPasswordCommand.execute(chainContextMock);

        verify(resetPasswordServiceMock).sendResetPasswordMessageTo(user, resetReason);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
        verify(audit).log(auditLogEvent);
    }

    @Test
    public void execute_EmailUnknown_WillNotSendMailButWillRedirect() throws Exception {
        when(chainContextMock.getEmail()).thenReturn(Optional.of("someEmail@bla.com"));
        when(credentialServiceMock.findUserByMail(emailFactory.email("someEmail@bla.com"))).thenReturn(Optional.empty());
        AuditLogEvent auditLogEvent = mock(AuditLogEvent.class);
        when(auditLogEventFactory.createEventForUserAuthentication(isNull(), anyString())).thenReturn(auditLogEvent);

        Command.State state = resetPasswordCommand.execute(chainContextMock);

        verifyZeroInteractions(resetPasswordServiceMock);
        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToPasswordReset();
        verify(audit).log(auditLogEvent);
    }

    @Test
    @Ignore
    public void postProcess_IfInvalidEmailAddressException_ThenRedirectWithInvalidEmailParam() throws Exception {
        throw new Exception("Implement me");
    }
}
