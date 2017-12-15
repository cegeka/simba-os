package org.simbasecurity.core.service.communication.reset.password;

import org.simbasecurity.core.domain.communication.token.ResetPasswordUserToken;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.USER_CREATION_USERTOKEN_EXPIRATION_TIME;

@Service
public class ResetPasswordByManager extends ResetPasswordReason{

    @Value("${simba.forgot.password.mail.template}")
    private String mailTemplate;

    @Override
    public UserToken createToken(Token token, long userId) {
        return new ResetPasswordUserToken(token, userId, expiresOn(USER_CREATION_USERTOKEN_EXPIRATION_TIME));
    }

    @Override
    public String getTemplate() {
        return mailTemplate;
    }
}
