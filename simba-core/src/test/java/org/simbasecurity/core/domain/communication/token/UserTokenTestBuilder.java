package org.simbasecurity.core.domain.communication.token;

import org.simbasecurity.core.util.dates.DateUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserTokenTestBuilder {
    private Token token;
    private long userId;
    private LocalDateTime expiresOn;

    private UserTokenTestBuilder() {
        expiresOn = DateUtils.now().plus(10, ChronoUnit.MINUTES);
    }

    public static UserTokenTestBuilder userToken() {
        return new UserTokenTestBuilder();
    }

    public UserToken buildResetPasswordUserToken() {
        return new ResetPasswordUserToken(token, userId, expiresOn);
    }

    public UserToken buildUserCreationUserToken() {
        return new UserCreationUserToken(token, userId, expiresOn);
    }

    public UserTokenTestBuilder withToken(Token token) {
        this.token = token;
        return this;
    }

    public UserTokenTestBuilder withUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public UserTokenTestBuilder withExpiresOn(LocalDateTime expiresOn) {
        this.expiresOn = expiresOn;
        return this;
    }

    public UserTokenTestBuilder withNewToken() {
        this.token = Token.generateToken();
        return this;
    }
}