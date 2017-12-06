package org.simbasecurity.core.service.communication.token;

import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.communication.token.UserToken.userToken;

public class TokenManagerTest extends PersistenceTestCase {

    @Autowired
    private UserTokenRepository userTokenRepository;

    private UserTokenService tokenManager;

    @Before
    public void setUp() throws Exception {
        tokenManager = new UserTokenService(userTokenRepository);
    }

    @Test
    public void generateToken_NoTokenExists_GeneratesANewTokenForTheGivenUserAndPersistsItForTheGivenUser() throws Exception {
        User user = aDefaultUser().build();

        Token token = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken).containsExactly(token);
    }

    @Test
    public void generateToken_TokenExistsForGivenUser_GeneratesANewTokenForTheGivenUserAndOverwritesTheExistingToken() throws Exception {
        User user = aDefaultUser().withId(665L).build();
        UserToken userToken = userToken(Token.generateToken(), user.getId());
        persistAndRefresh(userToken);

        Token token = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken).containsExactly(token);
    }
}