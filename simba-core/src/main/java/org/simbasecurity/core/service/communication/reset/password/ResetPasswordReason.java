package org.simbasecurity.core.service.communication.reset.password;

import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.simbasecurity.core.util.dates.DateUtils.now;

public abstract class ResetPasswordReason {

    @Inject
    @Named("configurationService")
    private CoreConfigurationService coreConfigurationService;

    public abstract UserToken createToken(Token token, long userId);

    abstract String getTemplate();


    LocalDateTime expiresOn(SimbaConfigurationParameter configurationParameter) {
        return now().plus(userTokenExpirationDelayInMillis(configurationParameter), ChronoUnit.MILLIS);
    }

    private long userTokenExpirationDelayInMillis(SimbaConfigurationParameter resetPasswordUsertokenExpirationTime) {
        Integer expirationDelay = coreConfigurationService.getValue(resetPasswordUsertokenExpirationTime);
        return TimeUnit.MILLISECONDS.convert(expirationDelay, resetPasswordUsertokenExpirationTime.getTimeUnit());
    }

    public String getMessage() {
        return "Email has been sent to user for following reason: "+getClass().getSimpleName();
    }
}
