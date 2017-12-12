package org.simbasecurity.core.service.communication.token;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.*;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.core.util.dates.DateUtils;
import org.simbasecurity.test.PersistenceTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.simbasecurity.core.domain.UserTestBuilder.aDefaultUser;
import static org.simbasecurity.core.domain.communication.token.UserTokenTestBuilder.userToken;
import static org.simbasecurity.core.util.dates.DateUtils.now;

public class UserTokenServiceTest extends PersistenceTestCase {

    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private UserTokenFactory userTokenFactoryMock;

    private UserTokenService tokenManager;

    @Before
    public void setUp() {
        userTokenFactoryMock = implantMock(UserTokenFactory.class);
        tokenManager = new UserTokenService(userTokenRepository, userTokenFactoryMock, userRepository);
    }

    @Test
    public void generateToken_noTokenExists_generatesANewToken_persistsItForTheGivenUser() {
        User user = aDefaultUser().withId(665L).build();
        UserToken userToken =  userToken().withNewToken().withUserId(user.getId()).buildResetPasswordUserToken();

        ArgumentCaptor<Token> generatedTokenCaptor = ArgumentCaptor.forClass(Token.class);
        when(userTokenFactoryMock.resetPasswordUserToken(generatedTokenCaptor.capture(),isA(Long.class))).thenReturn((ResetPasswordUserToken) userToken);

        Token generatedToken = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).containsExactly(userToken);
        assertThat(generatedToken).isEqualTo(generatedTokenCaptor.getValue());
    }

    @Test
    public void generateToken_tokenExistsForGivenUser_generatesANewToken_overwritesTheExistingToken() {
        User user = aDefaultUser().withId(665L).build();
        Token initialToken = Token.generateToken();
        LocalDateTime expiresTommorow = now().plusDays(1L);
        UserToken userToken = userToken().withToken(initialToken).withUserId(user.getId()).withExpiresOn(expiresTommorow).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Token generatedToken = tokenManager.generateToken(user);

        assertThat(userTokenRepository.findAll()).containsOnly(userToken);
        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken, UserToken::getExpiresOn).containsExactly(tuple(generatedToken,expiresTommorow));

        assertThat(generatedToken).isNotEqualTo(initialToken);
    }

    @Test
    public void getUserForToken() {
        User user = aDefaultUser().build();
        persistAndRefresh(user);

        Token token = Token.fromString("token");
        UserToken userToken = userToken().withToken(token).withUserId(user.getId()).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Optional<User> maybeUser = tokenManager.getUserForToken(token);

        Assertions.assertThat(maybeUser).contains(user);
    }

    @Test
    public void getUserForToken_tokenIsExpired_noTokenReturned() {
        User user = aDefaultUser().build();
        persistAndRefresh(user);

        Token token = Token.fromString("token");
        UserToken userToken = userToken().withToken(token).withUserId(user.getId()).withExpiresOn(LocalDateTime.now().minusMinutes(10)).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Optional<User> maybeUser = tokenManager.getUserForToken(token);

        Assertions.assertThat(maybeUser).isEmpty();
    }

    @Test
    public void purgeExpiredTokens_expiredTokenGetsDeleted() {
        User user = aDefaultUser().build();
        User newUser = aDefaultUser().build();
        persistAndRefresh(user, newUser);

        UserToken expiredUserToken = userToken().withNewToken().withUserId(user.getId()).withExpiresOn(DateUtils.now().minusDays(1L)).buildResetPasswordUserToken();
        UserToken userToken = userToken().withNewToken().withUserId(newUser.getId()).buildResetPasswordUserToken();
        persistAndRefresh(expiredUserToken, userToken);

        tokenManager.purgeExpiredTokens();

        assertThat(userTokenRepository.findAll()).containsExactly(userToken);
    }
}