package org.simbasecurity.core.domain.repository.communication.token;

import org.junit.Test;
import org.simbasecurity.core.domain.communication.token.ResetPasswordUserToken;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserCreationUserToken;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.simbasecurity.core.util.dates.DateUtils;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.simbasecurity.core.domain.communication.token.UserTokenTestBuilder.userToken;

public class UserTokenRepositoryTest extends PersistenceTestCase {

    protected CoreConfigurationService configurationServiceMock;
    @Autowired
    private UserTokenRepository userTokenRepository;

    @Test
    public void findByUserId_IfUserTokenExistsReturnToken() throws Exception {
        long userId = 63106510L;
        Token token = Token.generateToken();
        UserToken expectedUserToken = userToken().withToken(token).withUserId(userId).buildResetPasswordUserToken();
        persistAndRefresh(expectedUserToken);

        Optional<UserToken> userToken = userTokenRepository.findByUserId(userId);

        assertThat(userToken).contains(expectedUserToken);
    }

    @Test
    public void findById_CanLookupResetPasswordUserToken() throws Exception {
        long userId = 63106510L;
        LocalDateTime expiresOn = DateUtils.on(2017, 12, 8, 5, 0, 1);
        UserToken expectedUserToken = userToken().withNewToken().withExpiresOn(expiresOn).withUserId(userId).buildResetPasswordUserToken();
        persistAndRefresh(expectedUserToken);

        UserToken loadedUserToken = userTokenRepository.lookUp(expectedUserToken.getId());
        assertThat(loadedUserToken).isInstanceOf(ResetPasswordUserToken.class);
        assertThat(loadedUserToken.getExpiresOn()).isEqualTo(expiresOn);
    }

    @Test
    public void findById_CanLookupUserCreationUserToken() throws Exception {
        long userId = 63106510L;
        LocalDateTime expiresOn = DateUtils.on(2017, 12, 8, 5, 0, 1);
        UserToken expectedUserToken = userToken().withNewToken().withExpiresOn(expiresOn).withUserId(userId).buildUserCreationUserToken();
        persistAndRefresh(expectedUserToken);

        UserToken loadedUserToken = userTokenRepository.lookUp(expectedUserToken.getId());
        assertThat(loadedUserToken).isInstanceOf(UserCreationUserToken.class);
        assertThat(loadedUserToken.getExpiresOn()).isEqualTo(expiresOn);
    }


    @Test
    public void findByUserId_IfUserTokenDoesNotExistsReturnEmpty() throws Exception {
        Optional<UserToken> userToken = userTokenRepository.findByUserId(1L);

        assertThat(userToken).isEmpty();
    }

    @Test
    public void deleteToken() throws Exception {
        long userId = 0;
        Token token = Token.generateToken();
        UserToken userToken = userToken().withToken(token).withUserId(userId).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        assertThat(userTokenRepository.findByToken(token)).contains(userToken);

        userTokenRepository.deleteToken(token);

        assertThat(userTokenRepository.findByToken(token)).isEmpty();
    }

}