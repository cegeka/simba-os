package org.simbasecurity.core.service.communication.mail;

import org.simbasecurity.core.domain.user.EmailAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.logging.Logger;

public class SmtpMailService implements MailService {

    private static final Logger logger = Logger.getLogger(SmtpMailService.class.getName());

    private String mailFromAddress;
    private JavaMailSender javaMailSender;

    public SmtpMailService(JavaMailSender javaMailSender, String mailFromAddress) {
        this.javaMailSender = javaMailSender;
        this.mailFromAddress = mailFromAddress;
    }

    @Override
    public void sendMail(EmailAddress email, URL url) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            mimeMessage.addRecipients(Message.RecipientType.TO, email.asString());
            mimeMessage.addFrom(InternetAddress.parse(mailFromAddress));
            mimeMessage.setSubject("Password reset");
            mimeMessage.setText(url.toString());
            javaMailSender.send(mimeMessage);
            logger.info("Reset password mail has been send");
        } catch (MessagingException e) {
            throw new RuntimeException(String.format("Something went wrong when sending the mail to %s because %s", email, e.getCause()));
        }
    }
}
