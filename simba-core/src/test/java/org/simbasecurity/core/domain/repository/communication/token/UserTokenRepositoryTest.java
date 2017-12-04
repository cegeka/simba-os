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
    public void findByUserId_HappyPath() throws Exception {
        long userId = 0;
        UserToken expectedUserToken = UserToken.userToken(Token.generateToken(), userId);
        persistAndRefresh(expectedUserToken);

        Optional<UserToken> userToken = userTokenRepository.findByUserId(userId);

        assertThat(userToken).contains(expectedUserToken);
    }
}