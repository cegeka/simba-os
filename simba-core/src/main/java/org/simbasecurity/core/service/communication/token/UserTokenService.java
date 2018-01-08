package org.simbasecurity.core.service.communication.token;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordReason;
import org.simbasecurity.core.util.dates.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Service
@Transactional
public class UserTokenService {

    private static final Logger logger = Logger.getLogger(UserTokenService.class.getName());
    private UserTokenRepository userTokenRepository;
    private UserRepository userRepository;

    @Autowired
    public UserTokenService(UserTokenRepository userTokenRepository, UserRepository userRepository) {
        this.userTokenRepository = userTokenRepository;
        this.userRepository = userRepository;
    }

    public Token generateToken(User user, ResetPasswordReason reason) {
        Token token = Token.generateToken();
        userTokenRepository.findByUserId(user.getId())
                           .map(UserToken::getToken)
                           .ifPresent(this::deleteToken);
        userTokenRepository.persist(reason.createToken(token, user.getId()));
        logger.info("Token generated");
        return token;
    }

    public Optional<User> getUserForToken(Token token) {
        return userTokenRepository.findByToken(token)
                .filter(isNotExpired())
                .map(UserToken::getUserId)
                .flatMap(id -> userRepository.findById(id));
    }

    public void deleteToken(Token token) {
        userTokenRepository.deleteToken(token);
    }

    public void purgeExpiredTokens() {
        userTokenRepository.findAll().stream()
                .filter(isExpired())
                .map(UserToken::getToken)
                .forEach(this::deleteToken);
    }

    private Predicate<UserToken> isExpired() {
        return userToken -> userToken.getExpiresOn().isBefore(DateUtils.now());
    }

    private Predicate<UserToken> isNotExpired() {
        return isExpired().negate();
    }
}
