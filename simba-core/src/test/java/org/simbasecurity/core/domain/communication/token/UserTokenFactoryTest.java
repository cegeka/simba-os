package org.simbasecurity.core.domain.communication.token;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.util.dates.SystemDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME;
import static org.simbasecurity.core.config.SimbaConfigurationParameter.USER_CREATION_USERTOKEN_EXPIRATION_TIME;
import static org.simbasecurity.core.domain.communication.token.Token.generateToken;
import static org.simbasecurity.core.util.dates.DateUtils.on;

@RunWith(MockitoJUnitRunner.class)
public class UserTokenFactoryTest {

    @Rule public SystemDate systemDate = new SystemDate();

    @Mock
    private CoreConfigurationService coreConfigurationService;

    @InjectMocks
    private UserTokenFactory userTokenFactory;

    @Test
    public void resetPasswordUserToken_GebruiktResetPasswordExpirationTime_ConfigurationParameter(){
        when(coreConfigurationService.getValue(RESET_PASSWORD_USERTOKEN_EXPIRATION_TIME)).thenReturn(10);
        Token token = generateToken();

        LocalDateTime tokenGenerationTime = on(2017, 12, 8, 13, 18, 45);
        systemDate.freeze(tokenGenerationTime);
        ResetPasswordUserToken resetPasswordUserToken = userTokenFactory.resetPasswordUserToken(token, 1337);

        assertThat(resetPasswordUserToken.getToken()).isEqualTo(token);

        LocalDateTime expirationTime = tokenGenerationTime.plus(10, ChronoUnit.MINUTES);

        assertThat(resetPasswordUserToken.getExpiresOn()).isEqualTo(expirationTime);
    }

    @Test
    public void userCreationUserToken_GebruiktUserCreationExpirationTime_ConfigurationParameter(){
        when(coreConfigurationService.getValue(USER_CREATION_USERTOKEN_EXPIRATION_TIME)).thenReturn(3);
        Token token = generateToken();

        LocalDateTime tokenGenerationTime = on(2017, 12, 8, 13, 18, 45);
        systemDate.freeze(tokenGenerationTime);
        UserCreationUserToken userCreationUserToken = userTokenFactory.userCreationUserToken(token, 1337);

        assertThat(userCreationUserToken.getToken()).isEqualTo(token);

        LocalDateTime expirationTime = tokenGenerationTime.plus(3, ChronoUnit.DAYS);
        assertThat(userCreationUserToken.getExpiresOn()).isEqualTo(expirationTime);
    }

}