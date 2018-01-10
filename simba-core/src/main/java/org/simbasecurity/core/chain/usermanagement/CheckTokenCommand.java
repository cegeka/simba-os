package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.simbasecurity.core.chain.Command.State.CONTINUE;
import static org.simbasecurity.core.chain.Command.State.FINISH;

@Component
public class CheckTokenCommand implements Command {

    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CredentialService credentialService;

    @Override
    public State execute(ChainContext context) throws Exception {
        Optional<User> userFromToken = context.getToken()
                .map(Token::fromString)
                .flatMap(token -> userTokenService.getUserForToken(token));
        Optional<User> userFromEmail =
                context.getEmail().map(EmailAddress::email)
                        .flatMap(email -> credentialService.findUserByMail(email));
        if (!userFromEmail.isPresent()) {
            //log with provided email
            context.redirectToWrongToken();
            return FINISH;
        }
        if (!userFromToken.isPresent()){
            //log with user of provided email
            context.redirectToWrongToken();
            return FINISH;
        }
        if (!userFromToken.get().equals(userFromEmail.get())){
            //log with both users
            context.redirectToWrongToken();
            return FINISH;
        }
        userFromToken.ifPresent(user -> context.setUserName(user.getUserName()));
        return CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
