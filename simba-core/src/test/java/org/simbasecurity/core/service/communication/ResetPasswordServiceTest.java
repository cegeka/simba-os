package org.simbasecurity.core.service.communication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.communication.mail.MailService;
import org.simbasecurity.core.service.communication.mail.ResetPasswordService;
import org.simbasecurity.core.service.communication.mail.LinkGenerator;
import org.simbasecurity.core.service.communication.token.TokenGenerator;

import java.net.URL;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.user.EmailAddress.email;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordServiceTest {

    @Mock
    private LinkGenerator linkGeneratorMock;
    @Mock
    private TokenGenerator tokenGeneratorMock;
    @Mock
    private MailService mailServiceMock;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Test
    public void sendMesage_WillGenerateTokenAndLinkAndSendMail() throws Exception {
        EmailAddress email = email("something@mail.com");
        User user = aDefaultUser()
                .withEmail(email)
                .build();
        Token token = Token.generateToken();
        when(tokenGeneratorMock.generateToken(user)).thenReturn(token);
        URL link = new URL("http://www.google.com");
        when(linkGeneratorMock.generateResetPasswordLink(token)).thenReturn(link);

        resetPasswordService.sendMessage(user);

        verify(mailServiceMock).sendMail(email, link);
    }
}