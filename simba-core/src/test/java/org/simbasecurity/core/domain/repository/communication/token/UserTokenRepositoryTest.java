package org.simbasecurity.core.domain.repository.communication.token;

import org.junit.Test;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTokenRepositoryTest extends PersistenceTestCase {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Test
    public void findByUserId_IfUserTokenExistsReturnToken() throws Exception {
        long userId = 0;
        UserToken expectedUserToken = UserToken.userToken(Token.generateToken(), userId);
        persistAndRefresh(expectedUserToken);

        Optional<UserToken> userToken = userTokenRepository.findByUserId(userId);

        assertThat(userToken).contains(expectedUserToken);
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
        UserToken userToken = UserToken.userToken(token, userId);
        persistAndRefresh(userToken);

        assertThat(userTokenRepository.findByToken(token)).contains(userToken);

        userTokenRepository.deleteToken(token);

        assertThat(userTokenRepository.findByToken(token)).isEmpty();
    }
}