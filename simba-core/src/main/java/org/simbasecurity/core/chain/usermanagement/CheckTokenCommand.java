package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEvent;
import org.simbasecurity.core.audit.AuditLogEventFactory;
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

    @Autowired
    private Audit audit;
    @Autowired
    private AuditLogEventFactory auditLogEventFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        Optional<User> userFromToken = context.getToken()
                .map(Token::fromString)
                .flatMap(token -> userTokenService.getUserForToken(token));
        Optional<User> userFromEmail = context.getEmail()
                .map(EmailAddress::email)
                .flatMap(email -> credentialService.findUserByMail(email));

        if (noExistingUserForEmail(context, userFromEmail)) return FINISH;
        if (noExistingUserForToken(context, userFromEmail.get(), userFromToken)) return FINISH;
        if (existingUsersDoNotMatch(context, userFromEmail.get(), userFromToken.get())) return FINISH;

        User user = userFromToken.get();
        context.setUserName(user.getUserName());
        AuditLogEvent event = auditLogEventFactory.createEventForUserAuthentication(user.getUserName(), String.format("There was a successful reset password attempt for email address %s.", user.getEmail().asString()));
        audit.log(event);
        return CONTINUE;
    }

    private boolean noExistingUserForEmail(ChainContext context, Optional<User> userFromEmail) {
        if (!userFromEmail.isPresent()) {
            context.getEmail().ifPresent(emailInCtx -> {
                AuditLogEvent event = auditLogEventFactory.createEventForAuthentication(context, String.format("There was an unsuccessful reset password attempt for email address %s, but there was no user found for that email address.", emailInCtx));
                audit.log(event);
            });
            context.redirectToWrongToken();
            return true;
        }
        return false;
    }

    private boolean noExistingUserForToken(ChainContext context, User userFromEmail, Optional<User> userFromToken) {
        if (!userFromToken.isPresent()) {
            AuditLogEvent event = auditLogEventFactory.createEventForUserAuthentication(userFromEmail.getUserName(), String.format("There was an unsuccessful reset password attempt for email address %s, but there was no existing UserToken found for the emailUser associated with that email address.", userFromEmail.getEmail().asString()));
            audit.log(event);

            context.redirectToWrongToken();
            return true;
        }
        return false;
    }

    private boolean existingUsersDoNotMatch(ChainContext context, User userFromEmail, User userFromToken) {
        if (!userFromToken.equals(userFromEmail)) {
            AuditLogEvent event = auditLogEventFactory.createEventForAuthentication(context, String.format("There was an unsuccessful reset password attempt for email address %s, but the user associated with the token [%s] was different from the user associated with the email address [%s].", userFromEmail.getEmail().asString(), userFromToken.getUserName(), userFromEmail.getUserName()));
            audit.log(event);

            context.redirectToWrongToken();
            return true;
        }
        return false;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
