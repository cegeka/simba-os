package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.ERROR;
import static org.simbasecurity.core.chain.Command.State.FINISH;

@Component
public class CheckTokenCommand implements Command {

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public State execute(ChainContext context) throws Exception {
        Optional<User> mayBeUser = context.getToken()
                .map(Token::fromString)
                .flatMap(token -> userTokenService.getUserForToken(token));
        if (mayBeUser.isPresent()) {
            mayBeUser.ifPresent(user -> context.setUserName(user.getUserName()));
            return CONTINUE;
        } else {
            context.redirectToWrongToken();
            return FINISH;
        }
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
