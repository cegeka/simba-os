package org.simbasecurity.core.service.communication.token;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.UserTestBuilder;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.communication.token.UserToken.userToken;

public class UserTokenServiceTest extends PersistenceTestCase {

    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private UserTokenService userTokenService;

    @Before
    public void setUp() throws Exception {
        userTokenService = new UserTokenService(userTokenRepository, userRepository);
    }

    @Test
    public void generateToken_NoTokenExists_GeneratesANewTokenForTheGivenUserAndPersistsItForTheGivenUser() throws Exception {
        User user = aDefaultUser().build();

        Token token = userTokenService.generateToken(user);

        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken).containsExactly(token);
    }

    @Test
    public void generateToken_TokenExistsForGivenUser_GeneratesANewTokenForTheGivenUserAndOverwritesTheExistingToken() throws Exception {
        User user = aDefaultUser().withId(665L).build();
        UserToken userToken = userToken(Token.generateToken(), user.getId());
        persistAndRefresh(userToken);

        Token token = userTokenService.generateToken(user);

        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken).containsExactly(token);
    }

    @Test
    public void getUserForToken() throws Exception {
        User user = UserTestBuilder.aDefaultUser().build();
        persistAndRefresh(user);

        Token token = Token.fromString("token");
        UserToken userToken = userToken(token, user.getId());
        persistAndRefresh(userToken);

        Optional<User> maybeUser = userTokenService.getUserForToken(token);

        Assertions.assertThat(maybeUser).contains(user);
    }
}