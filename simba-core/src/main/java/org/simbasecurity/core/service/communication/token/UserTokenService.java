package org.simbasecurity.core.service.communication.token;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.UserRepository;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

import static org.simbasecurity.core.domain.communication.token.UserToken.userToken;

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

    public Token generateToken(User user) {
        Token token = Token.generateToken();
        Optional<UserToken> maybeUserToken = userTokenRepository.findByUserId(user.getId());
        if (maybeUserToken.isPresent()) {
            maybeUserToken.ifPresent(userToken -> userToken.setToken(token));
        } else {
            userTokenRepository.persist(userToken(token, user.getId()));
        }
        logger.info("Token generated");
        return token;
    }

    public Optional<User> getUserForToken(Token token) {
        return userTokenRepository.findByToken(token)
                .map(UserToken::getUserId)
                .flatMap(id -> userRepository.findById(id));
    }

    public void deleteToken(Token token) {
        userTokenRepository.deleteToken(token);
    }
}
