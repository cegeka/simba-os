package org.simbasecurity.core.service.communication;

import com.google.common.collect.ImmutableMap;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.service.communication.mail.LinkGenerator;
import org.simbasecurity.core.service.communication.mail.Mail;
import org.simbasecurity.core.service.communication.mail.MailService;
import org.simbasecurity.core.service.communication.mail.template.TemplateService;
import org.simbasecurity.core.service.communication.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.net.URL;

import static org.simbasecurity.core.domain.user.EmailAddress.email;
import static org.simbasecurity.core.service.communication.mail.Mail.mail;

@Transactional
@Service
public class ResetPasswordService {

    public static final String RESET_PASSWORD_SUBJECT = "reset password";

    @Autowired
    private MailService mailService;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private LinkGenerator linkGenerator;
    @Autowired
    private TemplateService templateService;

    @Value("${simba.smtp.mail.from}")
    private String resetPasswordFromAddress;
    @Value("${simba.reset.password.mail.template}")
    private String resetPasswordMailTemplate;

    public void sendMessage(User user) {
        Token token = tokenGenerator.generateToken(user);
        URL link = linkGenerator.generateResetPasswordLink(token);
        mailService.sendMail(createMail(user, link));
    }

    private Mail createMail(User user, URL link) {
        return mail()
                .from(email(resetPasswordFromAddress))
                .to(user.getEmail())
                .subject(RESET_PASSWORD_SUBJECT)
                .body(createBody(link.toString()));
    }

    private String createBody(String link){
        return templateService.createMailBody(resetPasswordMailTemplate, ImmutableMap.of("link", link));
    }

}
