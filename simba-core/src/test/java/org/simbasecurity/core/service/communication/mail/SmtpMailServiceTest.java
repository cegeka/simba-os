package org.simbasecurity.core.service.communication.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.domain.StubEmailFactory;
import org.simbasecurity.core.domain.user.EmailFactory;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;
import static org.simbasecurity.core.service.communication.mail.Mail.mail;

@RunWith(MockitoJUnitRunner.class)
public class SmtpMailServiceTest {

    @Mock private JavaMailSender javaMailSenderMock;

    @InjectMocks private SmtpMailService smtpMailService;

    private EmailFactory emailFactory = StubEmailFactory.emailRequired();

    @Test
    public void sendMail_SetsUpCorrectMimeMEssage() throws Exception {
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(javaMailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);

        smtpMailService.sendMail(mail()
                .to(emailFactory.email("test@hotmail.com"))
                .from(emailFactory.email("some-no-reply@cegeka.com"))
                .subject("Password reset")
                .body("http://www.google.com"));

        verify(mimeMessageMock).addRecipients(Message.RecipientType.TO, "test@hotmail.com");
        verify(mimeMessageMock).addFrom(InternetAddress.parse("some-no-reply@cegeka.com"));
        verify(mimeMessageMock).setSubject("Password reset");
        verify(mimeMessageMock).setContent("http://www.google.com", "text/html; charset=utf-8");
        verify(javaMailSenderMock).send(mimeMessageMock);
    }
}