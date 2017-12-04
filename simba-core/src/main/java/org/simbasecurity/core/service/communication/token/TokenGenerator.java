package org.simbasecurity.core.service.communication.token;

import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.communication.token.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.simbasecurity.core.domain.communication.token.UserToken.userToken;

@Service
public class TokenGenerator {

    private UserTokenRepository userTokenRepository;

    @Autowired
    public TokenGenerator(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public Token generateToken(User user) {
        Token token = Token.generateToken();
        Optional<UserToken> maybeUserToken = userTokenRepository.findByUserId(user.getId());
        if (maybeUserToken.isPresent()) {
            maybeUserToken.ifPresent(userToken -> userToken.setToken(token));
        } else {
            userTokenRepository.persist(userToken(token, user.getId()));
        }
        return token;
    }
}
