package org.simbasecurity.core.service.communication.token;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.*;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason;
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

    private ResetPasswordReason resetPasswordReason;

    private UserTokenService tokenManager;

    @Before
    public void setUp() {
        tokenManager = new UserTokenService(userTokenRepository, userRepository);
        resetPasswordReason = implantMock(ResetPasswordReason.class);
    }

    @Test
    public void generateToken_noTokenExists_generatesANewToken_persistsItForTheGivenUser() {
        User user = aDefaultUser().withId(665L).build();
        Token initialToken = Token.generateToken();
        UserToken userToken =  userToken().withToken(initialToken).withUserId(user.getId()).buildResetPasswordUserToken();
        when(resetPasswordReason.createToken(isA(Token.class), isA(Long.class))).thenReturn(userToken);

        Token generatedToken = tokenManager.generateToken(user, resetPasswordReason);

        assertThat(userTokenRepository.findAll()).containsExactly(userToken);
        assertThat(generatedToken).isNotEqualTo(initialToken);
    }

    @Test
    public void generateToken_tokenExistsForGivenUser_generatesANewToken_overwritesTheExistingToken() {
        User user = aDefaultUser().withId(665L).build();
        UserToken userToken = userToken().withNewToken().withUserId(user.getId()).buildResetPasswordUserToken();
        persistAndRefresh(userToken);

        Token newToken = Token.generateToken();
        LocalDateTime expiresTommorow = now().plusDays(1L);
        ResetPasswordUserToken resetToken = new ResetPasswordUserToken(newToken, user.getId(), expiresTommorow);
        when(resetPasswordReason.createToken(isA(Token.class), isA(Long.class))).thenReturn(resetToken);

        Token generatedToken = tokenManager.generateToken(user, resetPasswordReason);

        assertThat(userTokenRepository.findAll()).containsOnly(resetToken);
        assertThat(userTokenRepository.findAll()).extracting(UserToken::getToken, UserToken::getExpiresOn).containsExactly(tuple(newToken,expiresTommorow));
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
    public void purgeExpiredTokens_expiredTokensGetDeleted() {
        User catwoman = aDefaultUser().withFirstName("Selina").withName("Kyle").build();
        User theRiddler = aDefaultUser().withFirstName("Ed").withName("Nigma").build();
        User batman = aDefaultUser().build();
        persistAndRefresh(catwoman, batman, theRiddler);

        UserToken olderExpiredUserToken = userToken()
            .withNewToken()
            .withUserId(theRiddler.getId())
            .withExpiresOn(DateUtils.now()
            .minusDays(2L))
            .buildResetPasswordUserToken();
        UserToken expiredUserToken = userToken()
            .withNewToken()
            .withUserId(catwoman.getId())
            .withExpiresOn(DateUtils.now()
            .minusDays(1L))
            .buildResetPasswordUserToken();
        UserToken userToken = userToken()
            .withNewToken()
            .withUserId(batman.getId())
            .buildResetPasswordUserToken();
        persistAndRefresh(olderExpiredUserToken, expiredUserToken, userToken);

        tokenManager.purgeExpiredTokens();

        assertThat(userTokenRepository.findAll()).containsExactly(userToken);
    }
}