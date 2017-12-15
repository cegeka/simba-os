package org.simbasecurity.core.service.communication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.communication.mail.LinkGenerator;
import org.simbasecurity.core.service.communication.mail.MailService;
import org.simbasecurity.core.service.communication.mail.template.TemplateService;
import org.simbasecurity.core.service.communication.reset.password.ForgotPassword;
import org.simbasecurity.core.service.communication.reset.password.NewUser;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.simbasecurity.core.audit.AuditLogEventCategory.AUTHENTICATION;
import static org.simbasecurity.core.domain.Language.en_US;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.user.EmailAddress.email;
import static org.simbasecurity.core.exception.SimbaMessageKey.EMAIL_ADDRESS_REQUIRED;
import static org.simbasecurity.core.service.communication.mail.Mail.mail;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordServiceTest {

    @Mock private MailService mailServiceMock;
    @Mock private LinkGenerator linkGeneratorMock;
    @Mock private UserTokenService tokenManagerMock;
    @Mock private TemplateService templateServiceMock;
    @Mock private ForgotPassword forgotPasswordReason;
    @Mock private NewUser newUserReason;

    @Mock private Audit auditMock;
    @Spy private AuditLogEventFactory auditLogEventFactory;

    @InjectMocks private ResetPasswordService resetPasswordService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(resetPasswordService, "resetPasswordFromAddress", "bla@hotmail.com");
        when(forgotPasswordReason.getMessage()).thenReturn("Email has been sent to user for following reason: ForgotPassword");
        when(newUserReason.getMessage()).thenReturn("Email has been sent to user for following reason: NewUser");
    }

    @Test
    public void sendMessage_WillGenerateTokenAndLinkAndSendMail_WillTriggerAuditloggingForForgotPassword() throws Exception {
        EmailAddress email = email("something@mail.com");
        User user = aDefaultUser()
                .withUserName("test")
                .withEmail(email)
                .withLanguage(en_US)
                .build();
        Token token = Token.generateToken();
        when(tokenManagerMock.generateToken(user, forgotPasswordReason)).thenReturn(token);
        URL link = new URL("http://www.google.com");
        when(linkGeneratorMock.generateResetPasswordLink(token)).thenReturn(link);
        when(templateServiceMock.createMailBodyWithLink(forgotPasswordReason.getTemplate(), en_US, link)).thenReturn("someBody");

        resetPasswordService.sendResetPasswordMessageTo(user, forgotPasswordReason);

        verify(mailServiceMock).sendMail(mail().from(email("bla@hotmail.com")).to(email).subject("Reset password").body("someBody"));
        ArgumentCaptor<AuditLogEvent> captor = ArgumentCaptor.forClass(AuditLogEvent.class);
        verify(auditMock).log(captor.capture());
        assertThat(
                captor.getValue()).extracting(
                AuditLogEvent::getUsername,
                AuditLogEvent::getMessage,
                AuditLogEvent::getCategory
        ).containsExactly(
                "test",
                "Email has been sent to user for following reason: ForgotPassword",
                AUTHENTICATION
        );
    }

    @Test
    public void sendMessageForNewUser_WillTriggerAuditloggingForNewUser() throws Exception {
        EmailAddress email = email("something@mail.com");
        User user = aDefaultUser()
                .withUserName("otherTest")
                .withEmail(email)
                .withLanguage(en_US)
                .build();
        Token token = Token.generateToken();
        when(tokenManagerMock.generateToken(user, newUserReason)).thenReturn(token);
        URL link = new URL("http://www.google.com");
        when(linkGeneratorMock.generateResetPasswordLink(token)).thenReturn(link);
        when(templateServiceMock.createMailBodyWithLink(newUserReason.getTemplate(), en_US, link)).thenReturn("someBody");
        ArgumentCaptor<AuditLogEvent> logCaptor = ArgumentCaptor.forClass(AuditLogEvent.class);

        resetPasswordService.sendResetPasswordMessageTo(user, newUserReason);

        verify(mailServiceMock).sendMail(mail()
                .from(email("bla@hotmail.com"))
                .to(email)
                .subject("Reset password")
                .body("someBody")
        );
        verify(auditMock).log(logCaptor.capture());
        assertThat(
                logCaptor.getValue()).extracting(
                AuditLogEvent::getUsername,
                AuditLogEvent::getMessage,
                AuditLogEvent::getCategory
        ).containsExactly(
                "otherTest",
                "Email has been sent to user for following reason: NewUser",
                AUTHENTICATION
        );
    }

    @Test
    public void sendMessage_NoEmailAddress_ThrowsSimbaException() throws Exception {
        User user = aDefaultUser()
                .withEmail((EmailAddress) null)
                .build();

        assertThatExceptionOfType(SimbaException.class)
                .isThrownBy(() -> resetPasswordService.sendResetPasswordMessageTo(user, forgotPasswordReason))
                .withMessage(EMAIL_ADDRESS_REQUIRED.name());

        verifyZeroInteractions(auditMock);
    }
}