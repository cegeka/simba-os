package org.simbasecurity.core.service.communication.token;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.*;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.communication.token.UserTokenTestBuilder.userToken;

public class UserTokenServiceTest extends PersistenceTestCase {

    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private UserTokenFactory userTokenFactoryMock;

    private UserTokenService tokenManager;

    @Before
    public void setUp() throws Exception {
        userTokenFactoryMock = implantMock(UserTokenFactory.class);
        tokenManager = new UserTokenService(userTokenRepository, userTokenFactoryMock, userRepository);
    }

    @Test
    public void generateToken_NoTokenExists_GeneratesANewTokenForTheGivenUserAndPersistsItForTheGivenUser() throws Exception {
        User user = aDefaultUser().withId(665L).build();
        UserToken userToken =  userToken().withNewToken().withUserId(user.getId()).buildResetPasswordUserToken();

        ArgumentCaptor<Token> generatedTokenCaptor = ArgumentCaptor.forClass(Token.class);
        when(userTokenFactoryMock.resetPasswordUserToken(generatedTokenCaptor.capture(),isA(Long.class))).thenReturn((ResetPasswordUserToken) userToken);

        Token generatedToken = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).containsExactly(userToken);
        assertThat(generatedToken).isEqualTo(generatedTokenCaptor.getValue());
    }

    @Test
    public void generateToken_TokenExistsForGivenUser_GeneratesANewTokenForTheGivenUserAndOverwritesTheExistingToken() throws Exception {
        User user = aDefaultUser().withId(665L).build();
        Token initialToken = Token.generateToken();
        UserToken userToken = userToken().withToken(initialToken).withUserId(user.getId()).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Token generatedToken = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).containsOnly(userToken);
        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken).containsExactly(generatedToken);
        assertThat(generatedToken).isNotEqualTo(initialToken);
    }

    @Test
    public void getUserForToken() throws Exception {
        User user = aDefaultUser().build();
        persistAndRefresh(user);

        Token token = Token.fromString("token");
        UserToken userToken = userToken().withToken(token).withUserId(user.getId()).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Optional<User> maybeUser = tokenManager.getUserForToken(token);

        Assertions.assertThat(maybeUser).contains(user);
    }
}