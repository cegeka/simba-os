package org.simbasecurity.core.service.communication.reset.password;

import com.google.common.collect.ImmutableMap;
import org.simbasecurity.core.domain.Language;
import org.simbasecurity.core.service.communication.mail.template.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason.FORGOT_PASSWORD;

@Service
public class ResetPasswordTemplateService {
    @Value("${simba.forgot.password.mail.template}")
    private String forgotPasswordMailTemplate;
    @Value("${simba.new.user.mail.template}")
    private String newUserMailTemplate;

    @Autowired
    private TemplateService templateService;

    public String createMailBody(ResetPasswordReason reason, Language language, String link){
        String mailTemplate = getMailTemplate(reason);
        return templateService.createMailBody(mailTemplate, language, ImmutableMap.of("link", link));
    }

    private String getMailTemplate(ResetPasswordReason reason) {
        return reason == FORGOT_PASSWORD ? forgotPasswordMailTemplate : newUserMailTemplate;
    }
}
