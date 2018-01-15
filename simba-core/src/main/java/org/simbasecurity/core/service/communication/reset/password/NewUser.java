package org.simbasecurity.core.service.communication.reset.password;

import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserCreationUserToken;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.USER_CREATION_USERTOKEN_EXPIRATION_TIME;

@Service
public class NewUser extends ResetPasswordReason{

    @Value("${simba.new.user.mail.template}")
    private String mailTemplate;
    @Value("${simba.new.user.subject.template}")
    private String subjectTemplate;

    @Override
    public UserToken createToken(Token token, long userId) {
        return new UserCreationUserToken(token, userId, expiresOn(USER_CREATION_USERTOKEN_EXPIRATION_TIME));
    }

    @Override
    public String getTemplate() {
        return mailTemplate;
    }

    @Override
    public String getSubjectTemplate() {
        return subjectTemplate;
    }
}
