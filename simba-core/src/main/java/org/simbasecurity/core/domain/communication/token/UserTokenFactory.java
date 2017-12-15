package org.simbasecurity.core.domain.communication.token;

import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.USER_CREATION_USERTOKEN_EXPIRATION_TIME;
import static org.simbasecurity.core.util.dates.DateUtils.now;

@Service
public class UserTokenFactory {

    @Inject
    @Named("configurationService")
    private CoreConfigurationService coreConfigurationService;

    public ResetPasswordUserToken resetPasswordUserToken(Token token, long userId) {
        LocalDateTime expiresOn = now().plus(userTokenExpirationDelayInMillis(RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME), ChronoUnit.MILLIS);
        return new ResetPasswordUserToken(token, userId, expiresOn);
    }

    public UserCreationUserToken userCreationUserToken(Token token, long userId) {
        LocalDateTime expiresOn = now().plus(userTokenExpirationDelayInMillis(USER_CREATION_USERTOKEN_EXPIRATION_TIME), ChronoUnit.MILLIS);
        return new UserCreationUserToken(token, userId, expiresOn);
    }

    protected long userTokenExpirationDelayInMillis(SimbaConfigurationParameter resetPasswordUsertokenExpirationTime)  {
        Integer expirationDelay = getConfigurationService().getValue(resetPasswordUsertokenExpirationTime);
        return Duration.of(expirationDelay, resetPasswordUsertokenExpirationTime.getChronoUnit()).toMillis();
    }

    protected CoreConfigurationService getConfigurationService() {
        return coreConfigurationService;
    }
}
