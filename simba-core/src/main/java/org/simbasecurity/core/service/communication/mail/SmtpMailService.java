package org.simbasecurity.core.service.communication.mail;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Logger;

public class SmtpMailService implements MailService {

    private static final Logger logger = Logger.getLogger(SmtpMailService.class.getName());

    private JavaMailSender javaMailSender;

    public SmtpMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMail(Mail mail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            mimeMessage.addRecipients(Message.RecipientType.TO, mail.getTo().asString());
            mimeMessage.addFrom(InternetAddress.parse(mail.getFrom().asString()));
            mimeMessage.setSubject(mail.getSubject());
            mimeMessage.setContent(mail.getBody(), "text/html; charset=utf-8");
            javaMailSender.send(mimeMessage);
            logger.info("Reset password mail has been send");
        } catch (MessagingException e) {
            throw new RuntimeException(String.format("Something went wrong when sending mail %s because %s", mail, e.getCause()));
        }
    }
}
