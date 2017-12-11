package org.simbasecurity.core.domain.communication.token;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("resetPasswordUserToken")
public class ResetPasswordUserToken extends UserToken {

    ResetPasswordUserToken(Token token, long userId, LocalDateTime expiresOn) {
        super(token, userId, expiresOn);
    }

}
