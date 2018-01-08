package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.reset.password.ForgotPassword;
import org.simbasecurity.core.service.communication.reset.password.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The ResetPasswordCommand will send a mail to the given e-mail address by which the user can reset his password
 * executes the required actions.
 *
 * @since 3.1.7
 */
@Component
public class ResetPasswordCommand implements Command {

    @Autowired private ResetPasswordService resetPasswordService;
    @Autowired private CredentialService credentialService;
    @Autowired private ForgotPassword forgotPassword;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogEventFactory;

    @Override
    public State execute(ChainContext context) throws Exception {
        context.getEmail()
                .map(EmailAddress::email)
                .flatMap(email -> {
                    Optional<User> userByMail = credentialService.findUserByMail(email);
                    if (userByMail.isPresent()) {
                        audit.log(auditLogEventFactory.createEventForUserAuthentication(userByMail.get().getUserName(),
                                "Reset password email sent to " + email.asString()));
                    } else {
                        audit.log(auditLogEventFactory.createEventForUserAuthentication(null,
                                "Could not find user for " + email.asString() + " to send reset password email"));
                    }
                    return userByMail;
                })
                .ifPresent(user -> resetPasswordService.sendResetPasswordMessageTo(user, forgotPassword));
        context.redirectToPasswordReset();

        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        context.redirectToPasswordReset();
        return true;
    }
}
