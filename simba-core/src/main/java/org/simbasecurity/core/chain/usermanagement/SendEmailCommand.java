package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.simbasecurity.core.domain.User;
import org.simbasecurity.core.service.CredentialService;
import org.simbasecurity.core.service.communication.ResetPasswordMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.simbasecurity.common.constants.AuthenticationConstants.EMAIL;

/**
 * The ResetPasswordCommand will send a mail to the given e-mail adress by which the user can reset his password
 * executes the required actions.
 *
 * @since 3.1.7
 */
@Component
public class SendEmailCommand implements Command {

    @Autowired
    private ResetPasswordMailService resetPasswordMailService;
    @Autowired
    private CredentialService credentialService;

    @Override
    public State execute(ChainContext context) throws Exception {
        context.getEmail()
                .flatMap(email -> credentialService.findUserByMail(email))
                .ifPresent(user -> resetPasswordMailService.sendMessage(user));
        context.redirectToPasswordReset();
        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
