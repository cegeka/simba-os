package org.simbasecurity.core.chain.usermanagement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.simbasecurity.test.EmailRequiredRule;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.audit.AuditLogEventCategory.AUTHENTICATION;
import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.FINISH;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;

@RunWith(MockitoJUnitRunner.class)
public class CheckTokenCommandTest {

    @Rule
    public EmailRequiredRule emailRequired = EmailRequiredRule.emailRequired();

    @Mock
    private ChainContext chainContextMock;
    @Mock
    private UserTokenService userTokenServiceMock;
    @Mock
    private CredentialService credentialServiceMock;
    @Mock
    private Audit auditMock;
    @Spy
    private AuditLogEventFactory auditLogEventFactoryMock;

    @InjectMocks
    private CheckTokenCommand checkTokenCommand;

    @Captor
    private ArgumentCaptor<AuditLogEvent> auditEventCaptor;


    @Test
    public void execute_withoutTokenInContext_statusError() throws Exception {
        when(chainContextMock.getToken()).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
        verifyZeroInteractions(auditMock);
    }

    @Test
    public void execute_withoutEmailInContext_statusError() throws Exception {
        when(chainContextMock.getToken()).thenReturn(Optional.of("sleutel!"));
        when(chainContextMock.getEmail()).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();
        verifyZeroInteractions(auditMock);
    }

    @Test
    public void execute_withUnknownEmailAddress_statusError_AndProperAuditLogging() throws Exception {
        User user = aDefaultUser().withEmail("bruce@wayneindustries.com").build();
        setupContextWith("bruce@wayneindustries.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("bruce@wayneindustries.com"))).thenReturn(Optional.empty());

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();

        verify(auditMock).log(auditEventCaptor.capture());
        AuditLogEvent auditLogEvent = auditEventCaptor.getValue();
        assertThat(auditLogEvent.getCategory()).isEqualTo(AUTHENTICATION);
        assertThat(auditLogEvent.getMessage()).isEqualTo(String.format("There was an unsuccessful reset password attempt for email address %s, but there was no user found for that email address.",
                "bruce@wayneindustries.com"));
    }

    @Test
    public void execute_withKnownEmailAddress_ButUnknownToken_statusError_AndProperAuditLogging() throws Exception {
        User user = aDefaultUser().withUserName("batman").withEmail("bruce@wayneindustries.com").build();
        setupContextWith("bruce@wayneindustries.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.empty());
        when(credentialServiceMock.findUserByMail(EmailAddress.email("bruce@wayneindustries.com"))).thenReturn(Optional.of(user));

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();

        verify(auditMock).log(auditEventCaptor.capture());
        AuditLogEvent auditLogEvent = auditEventCaptor.getValue();
        assertThat(auditLogEvent.getCategory()).isEqualTo(AUTHENTICATION);
        assertThat(auditLogEvent.getUsername()).isEqualTo("batman");
        assertThat(auditLogEvent.getMessage()).isEqualTo(String.format("There was an unsuccessful reset password attempt for email address %s, but there was no existing UserToken found for the emailUser associated with that email address.",
                "bruce@wayneindustries.com"));
    }

    @Test
    public void execute_withTokenInContextAndDatabase_butEmailAddressUserIsDifferentFromTokenUser_statusError_AndProperAuditLogging() throws Exception {
        User user = aDefaultUser().withId(185L).withUserName("batman").withEmail("bruce@wayneindustries.com").build();
        User snarf = aDefaultUser().withId(665L).withUserName("snarf").withEmail("snarf@lioncats.com").build();
        setupContextWith("snarf@lioncats.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("snarf@lioncats.com"))).thenReturn(Optional.of(snarf));

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(FINISH);
        verify(chainContextMock).redirectToWrongToken();

        verify(auditMock).log(auditEventCaptor.capture());
        AuditLogEvent auditLogEvent = auditEventCaptor.getValue();
        assertThat(auditLogEvent.getCategory()).isEqualTo(AUTHENTICATION);
        assertThat(auditLogEvent.getMessage()).isEqualTo(String.format("There was an unsuccessful reset password attempt for email address %s, but the user associated with the token [%s] was different from the user associated with the email address [%s].",
                "snarf@lioncats.com",
                "batman",
                "snarf"));
    }

    @Test
    public void execute_withTokenInContextAndDatabase_EmailAddressUserIsSameAsTokenUser_statusContinue() throws Exception {
        User user = aDefaultUser().withUserName("batman").withEmail("bruce@wayneindustries.com").build();
        setupContextWith("bruce@wayneindustries.com", "sleutel!");

        when(userTokenServiceMock.getUserForToken(Token.fromString("sleutel!"))).thenReturn(Optional.of(user));
        when(credentialServiceMock.findUserByMail(EmailAddress.email("bruce@wayneindustries.com"))).thenReturn(Optional.of(user));

        Command.State state = checkTokenCommand.execute(chainContextMock);

        assertThat(state).isEqualTo(CONTINUE);
        verify(chainContextMock).setUserName("batman");

        verify(auditMock).log(auditEventCaptor.capture());
        AuditLogEvent auditLogEvent = auditEventCaptor.getValue();
        assertThat(auditLogEvent.getCategory()).isEqualTo(AUTHENTICATION);
        assertThat(auditLogEvent.getUsername()).isEqualTo("batman");
        assertThat(auditLogEvent.getMessage()).isEqualTo(String.format("There was a successful reset password attempt for email address %s.",
                "bruce@wayneindustries.com"));
    }

    private void setupContextWith(String email, String token) {
        when(chainContextMock.getToken()).thenReturn(Optional.of(token));
        when(chainContextMock.getEmail()).thenReturn(Optional.of(email));
    }
}