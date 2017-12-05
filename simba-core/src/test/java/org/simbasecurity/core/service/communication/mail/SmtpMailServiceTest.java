package org.simbasecurity.core.service.communication.mail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SmtpMailServiceTest {

    @Mock
    private JavaMailSender javaMailSenderMock;

    @InjectMocks
    private SmtpMailService smtpMailService;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(smtpMailService, "mailFromAddress", "some-no-reply@cegeka.com");
    }

    @Test
    public void name() throws MalformedURLException, MessagingException {
        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(javaMailSenderMock.createMimeMessage()).thenReturn(mimeMessageMock);

        smtpMailService.sendMail(EmailAddress.email("test@hotmail.com"), new URL("http://www.google.com"));

        verify(mimeMessageMock).addRecipients(Message.RecipientType.TO, "test@hotmail.com");
        verify(mimeMessageMock).addFrom(InternetAddress.parse("some-no-reply@cegeka.com"));
        verify(mimeMessageMock).setSubject("Password reset");
        verify(mimeMessageMock).setText("http://www.google.com");
        verify(javaMailSenderMock).send(mimeMessageMock);
    }
}