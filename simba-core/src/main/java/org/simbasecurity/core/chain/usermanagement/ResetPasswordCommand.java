package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.user.EmailAddress;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The ResetPasswordCommand will send a mail to the given e-mail address by which the user can reset his password
 * executes the required actions.
 *
 * @since 3.1.7
 */
@Component
public class ResetPasswordCommand implements Command {

    @Autowired
    private ResetPasswordService resetPasswordService;
    @Autowired
    private CredentialService credentialService;

    @Override
    public State execute(ChainContext context) throws Exception {
        context.getEmail()
                .map(EmailAddress::email)
                .flatMap(email -> credentialService.findUserByMail(email))
                .ifPresent(user -> resetPasswordService.sendMessage(user));
        context.redirectToPasswordReset();
        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
