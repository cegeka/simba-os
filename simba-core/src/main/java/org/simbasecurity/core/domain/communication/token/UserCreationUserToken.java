package org.simbasecurity.core.domain.communication.token;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("userCreationUserToken")
public class UserCreationUserToken extends UserToken {

    UserCreationUserToken(Token token, long userId, LocalDateTime expiresOn) {
        super(token, userId, expiresOn);
    }

}
