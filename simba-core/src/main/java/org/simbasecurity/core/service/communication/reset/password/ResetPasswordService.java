package org.simbasecurity.core.service.communication.reset.password;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.communication.mail.LinkGenerator;
import org.simbasecurity.core.service.communication.mail.Mail;
import org.simbasecurity.core.service.communication.mail.MailService;
import org.simbasecurity.core.service.communication.mail.template.TemplateService;
import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

import static org.simbasecurity.core.domain.user.EmailAddress.email;
import static org.simbasecurity.core.service.communication.mail.Mail.mail;

@Transactional
@Service
public class ResetPasswordService {

    @Autowired private MailService mailService;
    @Autowired private UserTokenService tokenManager;
    @Autowired private LinkGenerator linkGenerator;
    @Autowired private TemplateService templateService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogEventFactory;

    @Value("${simba.smtp.mail.from}")
    private String resetPasswordFromAddress;

    public void sendResetPasswordMessageTo(User user, ResetPasswordReason reason) {
        if (user.getEmail() == null) { throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);}

        Token token = tokenManager.generateToken(user, reason);
        URL link = linkGenerator.generateResetPasswordLink(user.getEmail(), token);
        String mailBody = templateService.createMailBodyWithLink(reason.getTemplate(), user.getLanguage(), link);
        String subject = templateService.createMailSubject(reason.getSubjectTemplate(), user.getLanguage());

        mailService.sendMail(createMail(user, mailBody, subject));
        audit.log(auditLogEventFactory.createEventForUserAuthentication(user.getUserName(), reason.getMessage()));
    }

    private Mail createMail(User user, String body, String subject) {
        return mail()
                .from(email(resetPasswordFromAddress))
                .to(user.getEmail())
                .subject(subject)
                .body(body);
    }
}
